package com.example.rabbitmq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RabbitmqApplication

fun main(args: Array<String>) {
	runApplication<RabbitmqApplication>(*args)
}
