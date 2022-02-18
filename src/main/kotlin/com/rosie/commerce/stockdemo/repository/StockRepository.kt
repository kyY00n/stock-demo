package com.rosie.commerce.stockdemo.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface StockRepository {
    suspend fun createProductStock(productId: String, amount: Long)
    suspend fun findStockByProductId(productId: String): String?
    suspend fun increaseStockByProductId(productId: String, amount: Long): Long?
    suspend fun decreaseStockByProductId(productId: String, amount: Long): Long?
}