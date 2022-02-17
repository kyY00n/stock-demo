package com.rosie.commerce.stockdemo.service

import com.rosie.commerce.stockdemo.controller.DecreaseProductStockReq
import com.rosie.commerce.stockdemo.controller.IncreaseProductStockReq
import com.rosie.commerce.stockdemo.repository.StockRepository
import org.springframework.stereotype.Service

data class SingleProductOrder(
    val productId: String,
    val quantity: Long,
)

@Service
class StockService(
    private val stockRepository: StockRepository
) {
    fun getProductStock(productId: String): Long {
        return stockRepository.findStockByProductId(productId)
    }

    fun incrProductStock(increaseProductStockReq: IncreaseProductStockReq): Long {
        val (productId, incr) = increaseProductStockReq
        return stockRepository.increaseStockByProductId(productId, incr)
    }

    fun decProductStock(decreaseProductStockReq: DecreaseProductStockReq): Long {
        val (productId, dec) = decreaseProductStockReq
        return stockRepository.decreaseStockByProductId(productId, dec)
    }

    fun createSingleProductOrder(order: SingleProductOrder) {
        val (productId, quantity) = order
        val currentStock = stockRepository.findStockByProductId(productId).block()?.toLong()
        stockRepository.decreaseStockByProductId(productId, quantity).block()

        val decreasedStock = currentStock?.let { currentStock - quantity } ?: throw Exception()

        if (decreasedStock < 0) {
            stockRepository.increaseStockByProductId(productId, quantity)
            // redis
        }
    }
}