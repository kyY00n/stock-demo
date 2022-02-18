package com.rosie.commerce.stockdemo.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Serializable
data class KafkaMessage(
    val event: RosieEvent,
    val author: String,
)

enum class RosieEvent {
    HI,
    BYE
}

@SpringBootTest
class KafkaProducerTest(
    @Autowired
    private val kafkaProducer: KafkaProducer
) {

    @Test
    fun `카프카 이벤트 쏘기`() {
        kafkaProducer.produceEvent("그냥 이벤트 쏘기", "안녕")
    }

    @Test
    fun `카프카에 json 쏘기`() {
        val message = KafkaMessage(
            RosieEvent.HI,
            "Rosie"
        )
        kafkaProducer.produceEvent("json 쏘기", Json.encodeToString(message))
    }
}