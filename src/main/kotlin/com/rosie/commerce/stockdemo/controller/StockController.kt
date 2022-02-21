package com.rosie.commerce.stockdemo.controller

import com.rosie.commerce.stockdemo.service.StockService
import org.springframework.web.bind.annotation.*

data class SetProductStockReq(
    val productId: String,
    val stock: Long,
)

data class IncreaseProductStockReq(
    val productId: String, // path variable 로 받으면 좋을 것 같다~
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

    @PostMapping("")
    suspend fun setNewProductStock(
        @RequestBody setProductStockReq: SetProductStockReq
    ): Unit {
        val (productId, stock) = setProductStockReq
        return stockService.createProductStock(productId, stock)
    }

    @GetMapping("/{productId}")
    suspend fun getProductStock(
        @PathVariable(value = "productId") productId: String
    ): Long? {
        return stockService.getProductStock(productId)
    }

    @PatchMapping("/incr")
    suspend fun increaseProductStock(
        @RequestBody increaseProductStockReq: IncreaseProductStockReq
    ): Long? {
        val changedStock = stockService.incrProductStock(increaseProductStockReq)
//        return ProductStockInfo(increaseProductStockReq.productId, changedStock)
        return changedStock
    }

    @PatchMapping("/dec")
    suspend fun decProductStock(
        @RequestBody decreaseProductStockReq: DecreaseProductStockReq
    ): Boolean {
        val changedStock = stockService.decProductStock(decreaseProductStockReq)
//        return ProductStockInfo(decreaseProductStockReq.productId, changedStock)
        return changedStock
    }
}