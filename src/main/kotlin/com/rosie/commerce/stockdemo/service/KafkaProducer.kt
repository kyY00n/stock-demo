package com.rosie.commerce.stockdemo.service

import com.rosie.commerce.stockdemo.event.StockChangeEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val TOPIC = "commerce-lab"

    fun produceEvent(key: String, message: String) {
        kafkaTemplate.send(TOPIC, key, message)
    }

    fun produceEvent(key: String, event: StockChangeEvent) {
        kafkaTemplate.send(TOPIC, key, Json.encodeToString(event))
    }
}