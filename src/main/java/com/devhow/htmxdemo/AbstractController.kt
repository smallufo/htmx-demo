/**
 * Created by smallufo on 2023-07-11.
 */
package com.devhow.htmxdemo

import org.springframework.web.bind.annotation.ModelAttribute
import java.time.Instant
import java.time.temporal.ChronoUnit


abstract class AbstractController {

    @ModelAttribute("now")
    fun now(): Instant {
        return Instant.now()
    }
}
