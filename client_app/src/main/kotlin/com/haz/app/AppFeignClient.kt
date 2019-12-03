package com.haz.app


import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "firstController", url = "http://localhost:8080")
interface AppFeignClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/first_controller"])
    fun getFirstController(): String

}