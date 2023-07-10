package com.devhow.htmxdemo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

@Controller
class DemoOverview {
    @GetMapping("/")
    fun overview(model: Model): String {
        model.addAttribute("now", Date())
        return "index"
    }
}
