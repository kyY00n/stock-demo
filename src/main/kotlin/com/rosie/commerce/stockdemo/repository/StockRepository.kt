package com.rosie.commerce.stockdemo.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface StockRepository {
    fun findStockByProductId(productId: String): Mono<String>
    fun increaseStockByProductId(productId: String, incr: Long): Mono<Long>
    fun decreaseStockByProductId(productId: String, dec: Long): Mono<Long>
}