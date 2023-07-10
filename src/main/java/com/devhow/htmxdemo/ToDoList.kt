package com.devhow.htmxdemo

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/public/todo")
class ToDoList : AbstractController() {

    private val logger = KotlinLogging.logger { }

    @GetMapping
    fun start(model: Model): String {
        model.addAttribute("item", "Get Stuff Done")
        return "todo"
    }

    @DeleteMapping(path = ["/delete"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun delete(): String {
        return ""
    }

    /**
     * Thymeleaf will let you use the fragment syntax in a controller, as shown below.
     * https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#defining-and-referencing-fragments
     */
    @PostMapping(path = ["/create"])
    fun create(@RequestParam("new-todo") todo: String?, model: Model): String {
        model.addAttribute("item", todo)

        logger.info { "created todo : $todo" }

        // Currently, IntelliJ doesn't recognize a Thymeleaf fragment returned in a controller.
        // https://youtrack.jetbrains.com/issue/IDEA-276625
        //
        return "todo::todo"
    }
}
