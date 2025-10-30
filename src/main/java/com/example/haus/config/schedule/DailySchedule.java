package com.example.haus.config.schedule;

import com.example.haus.repository.PromotionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailySchedule {

    PromotionRepository promotionRepository;

    @Scheduled(fixedRate = 300000)
    public void updateExpiredPromotion() {
        promotionRepository.updateExpiredPromotions(LocalDateTime.now());
    }
}
