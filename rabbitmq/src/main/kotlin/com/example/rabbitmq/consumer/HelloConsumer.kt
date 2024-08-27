package com.example.rabbitmq.consumer


import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
class HelloConsumer {
    private val logger = LoggerFactory.getLogger(HelloConsumer::class.java)

    @RabbitListener(queues = ["hello"])
    fun consumeMessage(message: String) {
        logger.info("Message received: $message")
    }

}