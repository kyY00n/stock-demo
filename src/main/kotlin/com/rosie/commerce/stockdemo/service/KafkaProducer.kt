package com.rosie.commerce.stockdemo.service

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
}