package com.devhow.htmxdemo

import org.intellij.lang.annotations.Language
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

/**
 * This demonstration uses HTML generated here (in the controller!) instead of just the HTML coming from Thymeleaf
 * templates.
 *
 *
 * This is really intended to be a very primitive transitional demonstration, showing the basics of how HTMX could
 * serve as the starting point for a more component-oriented approach, or perhaps even used in combination with
 * WebSockets and Server-Side Events.
 * https://htmx.org/docs/#websockets-and-sse
 *
 *
 * Put another way - this is a pretty messy, hacky mess... but it's also the kernel for starting what could be a
 * different approach.
 */
@Controller
@RequestMapping("/public/infinite-scroll")
class InfiniteScroll : AbstractController() {
    @GetMapping
    fun start(model: Model): String {
        return "infinite-scroll"
    }

    @Language("html")
    val contactHtml = """
             <tr>
                 <td>%s</td>
                 <td>%s</td>
                 <td>%s</td>
             </tr>
            """.trimIndent()

    @Language("html")
    val loadHtml = """
             <tr hx-get="/public/infinite-scroll/page/%d"
                 hx-trigger="revealed"
                 hx-swap="afterend">
                 <td>%s</td>
                 <td>%s</td>
                 <td>%s</td>
             </tr>
            """.trimIndent()

    @GetMapping(value = ["/page/{id}"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun nextPage(@PathVariable id: Int): String {
        Thread.sleep(500)
        return buildString {
            Contact.randomContacts(10).forEach { c ->
                append(contactHtml.format(c.firstName, c.lastName, c.email))
            }
            Contact.randomContacts(1)[0].also { c ->
                append(loadHtml.format(id + 1, c.firstName, c.lastName, c.email))
            }
        }
    }
}
