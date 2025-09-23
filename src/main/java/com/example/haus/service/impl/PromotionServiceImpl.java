package com.example.haus.service.impl;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.mapper.PromotionMapper;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.PromotionRepository;
import com.example.haus.repository.criteria.SearchCriteria;
import com.example.haus.repository.criteria.SearchQueryCriteriaConsumer;
import com.example.haus.service.PromotionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROMOTION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionServiceImpl implements PromotionService {

    PromotionRepository promotionRepository;
    PromotionMapper promotionMapper;
    CategoryRepository categoryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public PromotionResponseDto addPromotion(PromotionRequestDto requestDto) {
        // Kiểm tra code đã tồn tại chưa
        if (promotionRepository.existsByPromotionCode(requestDto.getPromotionCode())) {
            throw new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_EXISTED);
        }

        Promotion promotion = promotionMapper.promotionRequestDtoToPromotion(requestDto);

        // Nếu có categoryId → load Category managed entity
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
            promotion.setCategory(category);
        } else {
            promotion.setCategory(null);
        }

        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.promotionToPromotionResponseDto(saved);
    }

    @Override
    public PromotionResponseDto updatePromotion(Long id, PromotionRequestDto requestDto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));

        // Map các field khác
        promotionMapper.updatePromotionFromDto(requestDto, promotion);

        // Xử lý Category
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
            promotion.setCategory(category);
        } else {
            promotion.setCategory(null);
        }

        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.promotionToPromotionResponseDto(saved);
    }

    @Override
    public PromotionResponseDto getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        return promotionMapper.promotionToPromotionResponseDto(promotion);
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionResponseDto getPromotionByPromotionCode(String promotionCode) {
        Promotion promotion = promotionRepository.findByPromotionCode(promotionCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        return promotionMapper.promotionToPromotionResponseDto(promotion);
    }

    @Override
    public PaginationResponseDto<PromotionResponseDto> filterPromotions(PaginationRequestDto paginationRequest,
                                                                        String sortByPrice,
                                                                        String... search) {
        // Xử lý search criteria
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        if (search != null && search.length > 0) {
            Pattern pattern = Pattern.compile(AppConstants.SEARCH_OPERATOR);
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    searchCriteriaList.add(new SearchCriteria(
                            matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Promotion> promotions = getPromotions(paginationRequest.getPageNum(),
                paginationRequest.getPageSize(), searchCriteriaList, sortByPrice);

        Long totalElements = getTotalElements(searchCriteriaList);

        log.info("total Element = {}", totalElements);

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());
        Page<Promotion> pages = new PageImpl<>(promotions, pageable, totalElements);

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(paginationRequest.getPageNum() + 1)
                .pageSize(paginationRequest.getPageSize())
                .totalElement(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .sortType(sortByPrice)
                .sortBy(sortByPrice != null ? "price" : null)
                .build();

        List<PromotionResponseDto> promotionResponseDtoList = pages.getContent().stream()
                .map(promotionMapper::promotionToPromotionResponseDto)
                .toList();

        return PaginationResponseDto.<PromotionResponseDto>builder()
                .pageCustom(paginationCustom)
                .items(promotionResponseDtoList)
                .build();
    }

    private List<Promotion> getPromotions(int page, int size,
                                          List<SearchCriteria> searchCriteriaList,
                                          String sortByPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Promotion> cq = cb.createQuery(Promotion.class);
        Root<Promotion> root = cq.from(Promotion.class);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Promotion> consumer =
                new SearchQueryCriteriaConsumer<>(predicate, cb, root);
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        cq.where(predicate);

        if (sortByPrice != null) {
            if (sortByPrice.equalsIgnoreCase("asc")) {
                cq.orderBy(cb.asc(root.get("discountPercent")));
            } else {
                cq.orderBy(cb.desc(root.get("discountPercent")));
            }
        }

        return entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    private Long getTotalElements(List<SearchCriteria> searchCriteriaList) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Promotion> root = countQuery.from(Promotion.class);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Promotion> consumer =
                new SearchQueryCriteriaConsumer<>(predicate, cb, root);
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        countQuery.select(cb.count(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
