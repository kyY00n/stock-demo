package com.rosie.commerce.stockdemo.event

import com.rosie.commerce.stockdemo.service.KafkaProducer
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component

interface KafkaMessageFormat {}

enum class StockState(val description: String) {
    OUT_OF_STOCK("품절"),
    IN_STOCK("재고 있음")
}

@Serializable
data class StockChangeEvent(
    val productId: String,
    val state: StockState
): KafkaMessageFormat

@Component
class StockStateEventPublisher(
    private val kafkaProducer: KafkaProducer
) {
    suspend fun publishOutOfStockMsg(productId: String) {
        kafkaProducer.produceEvent(
            "stockChangeEvent:$productId",
            StockChangeEvent(productId, StockState.OUT_OF_STOCK)
        )
    }

    suspend fun publishToInStockMsg(productId: String) {
        kafkaProducer.produceEvent(
            "stockChangeEvent:$productId",
            StockChangeEvent(productId, StockState.IN_STOCK)
        )
    }
}