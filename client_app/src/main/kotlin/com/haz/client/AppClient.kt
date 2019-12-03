package com.haz.client

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class AppClient

fun main(args: Array<String>) {
    SpringApplication.run(AppClient::class.java, *args)
}