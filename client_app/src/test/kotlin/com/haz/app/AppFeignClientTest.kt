package com.haz.app

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AppFeignClientTest {

    @Autowired
    private lateinit var client: AppFeignClient

    @Test
    fun getTest(){
        println(client.getFirstController())
    }
}