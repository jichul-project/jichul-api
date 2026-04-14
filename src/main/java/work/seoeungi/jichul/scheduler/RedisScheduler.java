package work.seoeungi.jichul.scheduler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisScheduler {

    private final StringRedisTemplate redisTemplate;

    // Redis Cloud 삭제 방지
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void ping() {
        redisTemplate.opsForValue().set("ping", UUID.randomUUID().toString(), 1, TimeUnit.DAYS);
    }
}
