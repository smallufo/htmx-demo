package com.devhow.htmxdemo

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@RequestMapping("/private/")
class TopSecret {
    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("now", Date())
        return "private-index"
    }

    @GetMapping(path = ["/data"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun data(): String {
        return "<p>hi! %s </p>".formatted(Date().toString())
    }
}
