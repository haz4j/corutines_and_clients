package com.haz.app

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HtmlController {
    @GetMapping("/first_controller")
    fun firstController(): String {
        return "first_controller"
    }
}
