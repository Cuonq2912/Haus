package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceItemDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.product.ProductStatisticResponseDto;
import com.example.haus.domain.dto.response.statistic.BestSellerRow;
import com.example.haus.domain.dto.response.statistic.RecentOrderResponseDto;
import com.example.haus.domain.dto.response.statistic.RevenueDetailResponseDto;
import com.example.haus.domain.dto.response.statistic.StatisticResponseDto;
import com.example.haus.domain.entity.product.*;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.*;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderItemRepository;
import com.example.haus.repository.OrderRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.service.StatisticsService;
import com.example.haus.util.PaginationUtil;
import com.google.api.client.util.DateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.TemporalUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROMOTION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {

    OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    OrderItemRepository orderItemRepository;


    @Override
    public Map<String, Object> getOrderByMonth() {
        Map<String, Object> map = new HashMap<>();


        List <Order> sales = orderRepository.findAll();

        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).toLocalDate();
            LocalDate monthEnd = monthStart.plusMonths(1);

            double amount = sales.stream()
                    .filter(o -> !o.getOrderDate().isBefore(monthStart)
                            && o.getOrderDate().isBefore(monthEnd))
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            int monthNumber = monthStart.getMonthValue();
            monthlyRevenue.put(monthNumber, amount);
        }

        map.put("saleGraph", monthlyRevenue);

        return map;
    }

    @Override
    public Map<String, Object> getSales(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        int year = now.getYear();
        int quarter = (now.getMonthValue() - 1) / 3 + 1;

        List<Order> current = orderRepository.findByQuarter(quarter, year, startDate, endDate);

        int prevQuarter = (quarter == 1) ? 4 : quarter - 1;
        int prevYear = (quarter == 1) ? year - 1 : year;

        List<Order> previous = orderRepository.findByQuarter(prevQuarter, prevYear, startDate, endDate);

        final double[] total = {0.0, 0.0};
        final double[] totalPending = {0.0, 0.0};
        final double[] totalCompleted = {0.0, 0.0};
        final double[] totalReturned = {0.0, 0.0};

        Function<double[], Double> calcPercent = arr -> {
            double prev = arr[0];
            double curr = arr[1];

            if (prev == 0.0) return curr;
            return Math.ceil((curr - prev) / prev * 100);
        };

        current.forEach(order -> {
            switch (order.getStatus()) {
                case PENDING -> totalPending[1] += order.getTotalAmount();
                case COMPLETED -> totalCompleted[1] += order.getTotalAmount();
                case RETURNED -> totalReturned[1] += order.getTotalAmount();
            }
            total[1] += order.getTotalAmount();
        });

        previous.forEach(order -> {
            switch (order.getStatus()) {
                case PENDING -> totalPending[0] += order.getTotalAmount();
                case COMPLETED -> totalCompleted[0] += order.getTotalAmount();
                case RETURNED -> totalReturned[0] += order.getTotalAmount();
            }
            log.info("DEv = {}", order.getTotalAmount());
            total[0] += order.getTotalAmount();
        });

        map.put("totalOrders", RevenueDetailResponseDto.builder()
                .totalOrder(total[1])
                .percentIncrease(calcPercent.apply(total))
                .build());

        map.put("pendingOrders", RevenueDetailResponseDto.builder()
                .totalOrder(totalPending[1])
                .percentIncrease(calcPercent.apply(totalPending))
                .build());

        map.put("completedOrders", RevenueDetailResponseDto.builder()
                .totalOrder(totalCompleted[1])
                .percentIncrease(calcPercent.apply(totalCompleted))
                .build());

        map.put("returnOrders", RevenueDetailResponseDto.builder()
                .totalOrder(totalReturned[1])
                .percentIncrease(calcPercent.apply(totalReturned))
                .build());

        return map;
    }


    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<RecentOrderResponseDto> getRecentOrders(PaginationRequestDto paginationRequest, LocalDate startDate, LocalDate endDate) {

        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNum(),
                paginationRequest.getPageSize(),
                Sort.by("orderDate").descending());

        Page<Order> orderPage = orderRepository.findByDateTime(startDate, endDate, pageable);


        List<RecentOrderResponseDto> orderResponseList = orderPage.getContent().stream()
                .map(orderMapper::orderToRecentOrderResponseDto)
                .toList();

        return PaginationUtil.createPaginationResponse(orderPage, paginationRequest, orderResponseList);
    }

    @Override
    public PaginationResponseDto<ProductStatisticResponseDto> getBestSellers(PaginationRequestDto paginationRequest, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDataException("startDate is after endDate");
        }

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());

        Page<BestSellerRow> page = orderItemRepository.findBestSellers(startDate, endDate, pageable);

        List<Long> ids = page.getContent().stream()
                .map(BestSellerRow::getProductId)
                .toList();

        Map<Long, Long> soldMap = page.getContent().stream()
                .collect(Collectors.toMap(BestSellerRow::getProductId, BestSellerRow::getSoldQuantity));

        List<Product> productList = productRepository.findByIdIn(ids);

        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductStatisticResponseDto> dtos = ids.stream()
                .map(id -> {
                    Product product = productMap.get(id);
                    Long sold = soldMap.getOrDefault(id, 0L);

                    return ProductStatisticResponseDto.builder()
                            .id(product.getId())
                            .productName(product.getProductName())
                            .price(product.getPrice())
                            .image(product.getMedias().stream()
                                    .findFirst()
                                    .map(Media::getUrl)
                                    .orElse(null))
                            .soldQuantity(sold.intValue()) // hoặc đổi DTO sang Long cho chuẩn
                            .discountPercent(product.getCategories().stream()
                                    .map(Category::getPromotion)
                                    .filter(Objects::nonNull)
                                    .filter(p -> !p.getIsDeleted() && p.getStatus() == PromotionStatus.ACTIVE)
                                    .max(Comparator.comparing(Promotion::getDiscountPercent))
                                    .map(Promotion::getDiscountPercent)
                                    .orElse(0.0f))
                            .build();
                })
                .toList();


        return PaginationUtil.createPaginationResponse(page, paginationRequest, dtos);
    }
}
