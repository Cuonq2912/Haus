package com.example.haus.config.schedule;

import com.example.haus.repository.PromotionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "DAILY-SCHEDULE")
public class DailySchedule {

    PromotionRepository promotionRepository;

    @Scheduled(initialDelay = 0, fixedRate = 300000)
    public void updateExpiredPromotion() {
        log.info("Bắt đầu chạy tác vụ cập nhật khuyến mãi hết hạn..."); // Thêm log
        int count = promotionRepository.updateExpiredPromotions(LocalDate.now());
        log.info("Hoàn thành. Số lượng khuyến mãi hết hạn được cập nhật: {}", count);
    }
}
