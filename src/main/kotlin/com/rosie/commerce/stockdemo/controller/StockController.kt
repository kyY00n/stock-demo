package com.rosie.commerce.stockdemo.controller

import com.rosie.commerce.stockdemo.service.StockService
import org.springframework.web.bind.annotation.*

data class ProductStockInfo(
    val productId: String,
    val stock: Long,
)

data class IncreaseProductStockReq(
    val productId: String,
    val incr: Long,
)

data class DecreaseProductStockReq(
    val productId: String,
    val dec: Long,
)

@RestController
@RequestMapping("/stock")
class StockController(
    private val stockService: StockService
) {
    @GetMapping("/{productId}")
    fun getProductStock(
        @PathVariable(value = "productId") productId: String
    ): ProductStockInfo {
        val stock = stockService.getProductStock(productId)
        return ProductStockInfo(productId, stock)
    }

    @PatchMapping("/incr")
    fun increaseProductStock(
        @RequestBody increaseProductStockReq: IncreaseProductStockReq
    ): ProductStockInfo {
        val changedStock = stockService.incrProductStock(increaseProductStockReq)
        return ProductStockInfo(increaseProductStockReq.productId, changedStock)
    }

    @PatchMapping("/dec")
    fun decProductStock(
        @RequestBody decreaseProductStockReq: DecreaseProductStockReq
    ): ProductStockInfo {
        val changedStock = stockService.decProductStock(decreaseProductStockReq)
        return ProductStockInfo(decreaseProductStockReq.productId, changedStock)
    }
}