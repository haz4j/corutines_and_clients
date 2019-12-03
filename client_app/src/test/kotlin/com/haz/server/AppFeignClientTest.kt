package com.haz.server

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest()
class AppFeignClientTest {

    @Autowired
    private lateinit var firstApi: FirstApi


    @Test
    fun getTest(){
        println(firstApi.firstController())
    }
}