package com.example.rabbitmq.producer

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class HelloProducer (private val rabbitTemplate: RabbitTemplate){

    private val logger = LoggerFactory.getLogger(HelloProducer::class.java)

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    fun sendMessage(){
        val message = "Hello, RabbitMQ!"
        rabbitTemplate.convertAndSend("hello", message)
        logger.info("Message sent: $message")
    }

}