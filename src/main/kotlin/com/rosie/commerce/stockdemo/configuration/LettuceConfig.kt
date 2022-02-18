package com.rosie.commerce.stockdemo.configuration

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LettuceConfig(
    @Value("\${redis.host}")
    private val host: String,
    @Value("\${redis.port}")
    private val port: Int,
) {
    @Bean
    fun redisConnection(): StatefulRedisConnection<String, String> {
        val redisClient = RedisClient.create(RedisURI.create(host, port))
        return redisClient.connect()
    }
}