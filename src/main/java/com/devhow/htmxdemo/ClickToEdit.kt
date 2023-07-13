package com.devhow.htmxdemo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import mu.KotlinLogging

@Controller
@RequestMapping("/public/click-to-edit")
class ClickToEdit : AbstractController() {

  private val logger = KotlinLogging.logger { }

  private val demoContact = Contact.demoContact()

  @GetMapping
  fun start(model: Model): String {
    model.addAttribute("contact", demoContact)
    return "click-to-edit"
  }

  @PostMapping("/edit/{id}")
  fun editForm(contact: Contact?, model: Model, @PathVariable id: String?): String {
    model.addAttribute("contact", contact)
    model.addAttribute("id", id)
    return "click-to-edit-form"
  }

  @PostMapping("/commit")
  fun editPost(contact: Contact, model: Model): String {
    model.addAttribute("contact", contact)
    logger.info { "editPost , contact = $contact" }
    demoContact.apply {
      firstName = contact.firstName
      lastName = contact.lastName
      email = contact.email
    }

    return "click-to-edit-default"
  }
}
