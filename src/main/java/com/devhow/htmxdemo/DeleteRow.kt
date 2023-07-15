/**
 * Created by smallufo on 2023-07-16.
 */
package com.devhow.htmxdemo

import com.github.javafaker.Faker
import mu.KotlinLogging
import org.intellij.lang.annotations.Language
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/public/delete-row")
class DeleteRow {

    private val faker = Faker()

    private val logger = KotlinLogging.logger { }

    @Language("html")
    val contactHtml = """
             <tr>
                 <td>%s</td>
                 <td>%s</td>
                 <td>%s</td>
                 <td>
                    <button class="btn btn-danger" hx-delete="/public/delete-row/delete/%s">
                      Delete
                    </button>
                 </td>
             </tr>
            """.trimIndent()

    private val contacts: MutableList<Contact> by lazy {
        (1..5).map {
            Contact(
                faker.name().firstName(),
                faker.name().firstName(),
                faker.internet().safeEmailAddress()
            )
        }.toMutableList()
    }

    @GetMapping()
    fun index(model: Model): String {
        return "delete-row"
    }

    @GetMapping("/contacts")
    @ResponseBody
    fun contacts(): String {
        return contacts.joinToString("\n") { c ->
            contactHtml.format(c.firstName, c.lastName, c.email, c.email)
        }
    }

    @DeleteMapping("/delete/{email}")
    @ResponseBody
    fun deleteEmail(@PathVariable email: String) : String {
        logger.info { "deleting email = $email" }
        contacts.removeIf { it.email == email }
        return ""
    }

}
