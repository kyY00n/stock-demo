package com.rosie.commerce.stockdemo.service

import com.rosie.commerce.stockdemo.controller.DecreaseProductStockReq
import com.rosie.commerce.stockdemo.controller.IncreaseProductStockReq
import com.rosie.commerce.stockdemo.event.StockStateEventPublisher
import com.rosie.commerce.stockdemo.repository.StockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate

@ExtendWith(MockKExtension::class)
class StockServiceTest {
    internal class MockStockRepository {
        @SpyK
        @InjectMockKs
        private lateinit var stockService: StockService

        @MockK
        @InjectMockKs
        private lateinit var stockStateEventPublisher: StockStateEventPublisher

        @MockK
        private lateinit var stockRepository: StockRepository

        @Test
        fun `품절 상태에서 사장님이 재고를 추가하면 재고 추가 이벤트를 발행한다`() {
            // given
            val productId = "testProductId"
            val originalStock = 0L

            coEvery { stockStateEventPublisher.publishToInStockMsg(any()) } answers {
                println("published in-stock event")
            }

            val increaseAmount = 100L

            coEvery { stockRepository.findStockByProductId(productId) } returns originalStock
            val increaseProductStockReq = IncreaseProductStockReq(
                productId,
                increaseAmount
            )

            coEvery {
                stockRepository.increaseStockByProductId(
                    productId,
                    increaseAmount
                )
            } returns originalStock + increaseAmount

            // when
            runBlocking {
                stockService.incrProductStock(increaseProductStockReq)
            }

            // then
            coVerify {
                stockStateEventPublisher.publishToInStockMsg(productId)
            }
            coVerify {
                stockRepository.increaseStockByProductId(any(), any())
            }
        }


        @Test
        fun `품절 시 카프카 이벤트를 발행한다`() {
            // given
            val productId = "testProductId"
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
            val productId = "testProductId"
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

    @SpringBootTest
    internal class RealStockRepository(
        @Autowired
        private val stockService: StockService,
        @Autowired
        private val redisTemplate: ReactiveRedisTemplate<String, String>
    ) {

        private val productId = "testProductId"

        @BeforeEach
        fun `테스트 상품 재고 저장하기`() {
            val value = runBlocking { redisTemplate.opsForValue().set(productId, "10").awaitSingle() }
        }

        @Test
        fun `부하 테스트`() {
            // given\
            val stockList = arrayOf(4L, 3L, 2L, 4L, 5L, 2L, 2L)
            val res = ArrayList<Boolean>()


            // when
            runBlocking {
                coroutineScope {
                    (0..6).forEach { i ->
                        val wasSucceeded =
                            stockService.decProductStock(DecreaseProductStockReq(productId, stockList[i]))
                        res.add(wasSucceeded)
                    }
                }
            }

            // then
            res.mapIndexed { idx, b ->
                println("$idx. $b")
            }
            val currStock = runBlocking {
                redisTemplate.opsForValue().get(productId).awaitSingle()
            }
            println(currStock)
        }
    }
}