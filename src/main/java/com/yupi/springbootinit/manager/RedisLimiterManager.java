package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key 区分不同的key，比如不同的用户应该分开来考虑
     */
    public void doRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 3, 1, RateIntervalUnit.SECONDS);

        boolean acquire = rateLimiter.tryAcquire(1);
        if (!acquire) {
            throw new BusinessException(ErrorCode.TOO_MAY_REQUESTS);
        }
    }
}
