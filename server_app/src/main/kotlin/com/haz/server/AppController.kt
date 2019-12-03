package com.haz.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HtmlController : FirstApi {
    @GetMapping("/first_controller")
    override fun firstController(): String {
        return "first_controller"
    }
}
