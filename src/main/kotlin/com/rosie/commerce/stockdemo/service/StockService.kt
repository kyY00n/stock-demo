package com.rosie.commerce.stockdemo.service

import com.rosie.commerce.stockdemo.controller.DecreaseProductStockReq
import com.rosie.commerce.stockdemo.controller.IncreaseProductStockReq
import com.rosie.commerce.stockdemo.event.StockStateEventPublisher
import com.rosie.commerce.stockdemo.repository.StockRepository
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockStateEventPublisher: StockStateEventPublisher
) {
    suspend fun createProductStock(productId: String, quantity: Long) {
        stockRepository.createProductStock(productId, quantity)
    }

    suspend fun getProductStock(productId: String): String? {
        return stockRepository.findStockByProductId(productId)
    }

    suspend fun incrProductStock(increaseProductStockReq: IncreaseProductStockReq): Long? {
        val (productId, incr) = increaseProductStockReq
        return stockRepository.increaseStockByProductId(productId, incr)
    }

    suspend fun decProductStock(decreaseProductStockReq: DecreaseProductStockReq): Boolean {
        val (productId, dec) = decreaseProductStockReq
        val currStock = stockRepository.decreaseStockByProductId(productId, dec) ?: return false
        if (currStock < 0) {
            stockRepository.increaseStockByProductId(productId, dec)
            return false
        }
        if (currStock == 0L) {
            // TODO: 1 품절여부 갱신을 위한 품절 kafka 이벤트 발행
            stockStateEventPublisher.publishOutOfStockMsg(productId)
        }
        return true
    }
}