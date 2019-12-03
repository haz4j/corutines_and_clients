package com.haz.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class KotlinDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinDemoApplication::class.java, *args)
}

