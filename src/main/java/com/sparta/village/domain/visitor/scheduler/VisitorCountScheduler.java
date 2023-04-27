package com.sparta.village.domain.visitor.scheduler;

import com.sparta.village.domain.visitor.entity.Visitor;
import com.sparta.village.domain.visitor.repository.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class VisitorCountScheduler {
    private static final Logger logger = Logger.getLogger(VisitorCountScheduler.class.getName());

    private final RedisTemplate<String, Integer> redisTemplate;
    private final VisitorCountRepository visitorCountRepository;

    @Scheduled(cron = "0 * * * * *")
    public void saveVisitorCountToDb() {
        try {
            Visitor visitor = visitorCountRepository.findById(1L).orElseGet(() -> new Visitor(redisTemplate.opsForValue().get("visitor_count")));
            visitor.updateVisitorCount(redisTemplate.opsForValue().get("visitor_count"));
            visitorCountRepository.save(visitor);
        } catch (Exception e) {
            logger.severe("Failed to save visitor count to database.");
            e.printStackTrace();
        }
    }
}

