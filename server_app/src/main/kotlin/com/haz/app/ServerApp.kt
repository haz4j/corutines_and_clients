package com.haz.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ServerApp

fun main(args: Array<String>) {
    SpringApplication.run(ServerApp::class.java, *args)
}

