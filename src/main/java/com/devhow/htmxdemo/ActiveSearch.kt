/**
 * Created by smallufo on 2023-07-16.
 */
package com.devhow.htmxdemo

import com.github.javafaker.Faker
import mu.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*


@Controller
@RequestMapping("/public/active-search")
class ActiveSearch {

    private val logger = KotlinLogging.logger { }

    private val faker = Faker()

    private val contacts: List<Contact> by lazy {
        (1..100).map {
            Contact(
                faker.name().firstName(),
                faker.name().firstName(),
                faker.internet().safeEmailAddress()
            )
        }.toList()
    }

    @GetMapping()
    fun index(model: Model): String {
        return "active-search"
    }

    @GetMapping("/contacts")
    @ResponseBody
    fun contacts() : String {
        return contactsString(contacts)
    }

    @PostMapping("/search")
    @ResponseBody
    fun search(@RequestParam("keyword") keyword: String): String {
        logger.info { "keyword = $keyword" }
        val keywordLowerCase = keyword.lowercase()

        val results = contacts.filter { c ->
            c.firstName.lowercase(Locale.getDefault()).contains(keywordLowerCase) ||
                    c.lastName.lowercase(Locale.getDefault()).contains(keywordLowerCase) ||
                    c.email.lowercase(Locale.getDefault()).contains(keywordLowerCase)
        }
        return contactsString(results)
    }

    private fun contactsString(contacts : List<Contact>) : String {
        return contacts.joinToString("\n") {
            """
            <tr>
                <td>${it.firstName}</td>
                <td>${it.lastName}</td>
                <td>${it.email}</td>
            </tr>
            """.trimIndent()
        }
    }
}
