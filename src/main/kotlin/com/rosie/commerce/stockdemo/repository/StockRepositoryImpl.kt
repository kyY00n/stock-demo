package com.rosie.commerce.stockdemo.repository

import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.reactive.RedisReactiveCommands
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StockRepositoryImpl(
    private val redisConnection: StatefulRedisConnection<String, String>
): StockRepository {
    private val redisReactiveCommands: RedisReactiveCommands<String, String> = redisConnection.reactive()
    override suspend fun createProductStock(productId: String, amount: Long) {
        val existingValue = redisReactiveCommands.get(productId).awaitSingleOrNull() // existingValue의 value를 꺼내와야 null인지 아닌지 알 수 있다.
        if (existingValue != null) throw Exception()
        redisReactiveCommands.set(productId, amount.toString()).awaitSingle() // await을 해야 set 연산이 적용된 뒤 api 응답을 줄 수 있을 것이다.
        // 뭐.. await을 서비스에서 해도 될 것같은데 그냥 여기서 하자.
    }

    // 코루틴을 사용하면 마치 동기처럼! 사용할 수 있당
    override suspend fun findStockByProductId(productId: String): Long? {
        return redisReactiveCommands.get(productId).awaitSingleOrNull()?.toLong()
    }

    override suspend fun increaseStockByProductId(productId: String, amount: Long): Long? {
        return redisReactiveCommands.incrby(productId, amount).awaitSingleOrNull()
    }

    override suspend fun decreaseStockByProductId(productId: String, amount: Long): Long? {
        return redisReactiveCommands.decrby(productId, amount).awaitSingleOrNull()
    }
}