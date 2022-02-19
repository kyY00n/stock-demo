package com.rosie.commerce.stockdemo.service

import com.rosie.commerce.stockdemo.controller.DecreaseProductStockReq
import com.rosie.commerce.stockdemo.event.StockStateEventPublisher
import com.rosie.commerce.stockdemo.repository.StockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class StockServiceTest {
    @SpyK
    @InjectMockKs
    private lateinit var stockService: StockService

    @MockK
    @InjectMockKs
    private lateinit var stockStateEventPublisher: StockStateEventPublisher

    @MockK
    private lateinit var stockRepository: StockRepository


    @Test
    fun `품절 시 카프카 이벤트를 발행한다`() {
        // given
        val productId = "textProductId"
        val originalStock = 2L

        coEvery { stockStateEventPublisher.publishOutOfStockMsg(any()) } answers {
            println("published out of stock event")
        }

        val decreaseAmount = 2L

        coEvery {
            stockRepository.decreaseStockByProductId(productId, decreaseAmount)
        } returns originalStock - decreaseAmount

        coEvery {
            stockRepository.increaseStockByProductId(productId, decreaseAmount)
        } returns originalStock

        // when
        val decreaseProductStockReq = DecreaseProductStockReq(
            productId,
            decreaseAmount
        )
        val returnValue = runBlocking {
            stockService.decProductStock(decreaseProductStockReq)
        }

        // then
        assertTrue { returnValue }
        coVerify {
            stockStateEventPublisher.publishOutOfStockMsg(any())
        }

        coVerify { stockRepository.decreaseStockByProductId(productId, decreaseAmount) }
    }

    @Test
    fun `재고 부족으로 인한 주문 취소`() {
        // given
        val productId = "textProductId"
        val originalStock = 2L
        val decreaseAmount = 3L

        coEvery {
            stockRepository.decreaseStockByProductId(productId, decreaseAmount)
        } returns originalStock - decreaseAmount

        coEvery {
            stockRepository.increaseStockByProductId(productId, decreaseAmount)
        } returns originalStock

        // when
        val decreaseProductStockReq = DecreaseProductStockReq(
            productId,
            decreaseAmount
        )
        val returnValue = runBlocking {
            stockService.decProductStock(decreaseProductStockReq)
        }

        // then
        assertTrue { !returnValue }

        coVerify { stockRepository.decreaseStockByProductId(productId, decreaseAmount) }

        coVerify { stockRepository.increaseStockByProductId(productId, decreaseAmount) }

    }
}