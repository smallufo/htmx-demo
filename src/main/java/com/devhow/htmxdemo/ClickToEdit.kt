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
class ClickToEdit {

  private val logger = KotlinLogging.logger { }

  @GetMapping
  fun start(model: Model): String {
    model.addAttribute("contact", Contact.demoContact())
    model.addAttribute("now", Date().toInstant())
    return "click-to-edit"
  }

  @PostMapping("/edit/{id}")
  fun editForm(contact: Contact?, model: Model, @PathVariable id: String?): String {
    model.addAttribute("contact", contact)
    model.addAttribute("id", id)
    return "click-to-edit-form"
  }

  @PostMapping("/commit")
  fun editPost(contact: Contact?, model: Model): String {
    model.addAttribute("contact", contact)
    logger.info { "editPost , contact = $contact" }
    return "click-to-edit-default"
  }
}
