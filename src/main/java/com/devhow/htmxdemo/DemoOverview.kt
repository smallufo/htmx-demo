package com.devhow.htmxdemo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

@Controller
class DemoOverview : AbstractController() {
    @GetMapping("/")
    fun overview(model: Model): String {
        return "index"
    }
}
