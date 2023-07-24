/**
 * Created by smallufo on 2023-07-25.
 */
package com.devhow.htmxdemo

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@RequestMapping("/public/hx-swap")
class HxSwap : AbstractController() {

    private var count : Int = 0

    @GetMapping
    fun start(model: Model): String {
        return "hx-swap"
    }

    @GetMapping("/randomText")
    @ResponseBody
    fun randomText() : String {
        count++
        return "[$count] : "+ RandomStringUtils.randomAlphabetic(10)+"<br/>";
    }
}
