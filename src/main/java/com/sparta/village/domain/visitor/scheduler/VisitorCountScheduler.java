package com.sparta.village.domain.visitor.scheduler;

import com.sparta.village.domain.visitor.entity.Visitor;
import com.sparta.village.domain.visitor.repository.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorCountScheduler {
    private final RedisTemplate<String, Long> redisTemplate;
    private final VisitorCountRepository visitorCountRepository;

    @Scheduled(cron = "0 */30 * * * *") // 매일 자정마다 실행
    public void saveVisitorCountToDb() {
        Visitor visitor = visitorCountRepository.findById(1L).orElseGet(() -> new Visitor(redisTemplate.opsForValue().get("visitor_count")));
        visitor.updateVisitorCount(redisTemplate.opsForValue().get("visitor_count"));
        visitorCountRepository.save(visitor);
    }
}

