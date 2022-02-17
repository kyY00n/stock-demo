package com.rosie.commerce.stockdemo.repository

import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StockRepositoryImpl(
    private val redisOperations: ReactiveRedisOperations<String, String>
): StockRepository {
    private val opsForValue = redisOperations.opsForValue()

    override fun findStockByProductId(productId: String): Mono<String> {
        return opsForValue[productId]
    }

    override fun increaseStockByProductId(productId: String, incr: Long): Mono<Long> {
        return opsForValue.increment(productId)
    }

    override fun decreaseStockByProductId(productId: String, dec: Long): Mono<Long> {
        return opsForValue.decrement(productId)
    }
}