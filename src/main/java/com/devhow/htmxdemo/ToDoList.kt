package com.devhow.htmxdemo

import jakarta.inject.Inject
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

class TodoResetEvent : ApplicationEvent(Instant.now())

data class Todo(val id: Int, val title: String)

@Controller
@RequestMapping("/public/todo")
class ToDoList : AbstractController() {

    @Inject
    private lateinit var publisher: ApplicationEventPublisher

    private val logger = KotlinLogging.logger { }

    private val todos: MutableList<Todo> = mutableListOf()

    @EventListener(ApplicationReadyEvent::class, TodoResetEvent::class)
    fun reset() {
        todos.clear()
        todos.add(Todo(1, "Default work"))
    }

    @GetMapping
    fun start(model: Model): String {
        model.addAttribute("todos", todos)
        return "todo"
    }

    @DeleteMapping(path = ["/delete/{id}"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun delete(@PathVariable id: Int): String {
        todos.removeIf { it.id == id }
        logger.info { "deleted $id" }
        return ""
    }

    @PostMapping(path = ["/create"])
    fun create(@RequestParam("new-todo") title: String, model: Model): String {

        val newId = todos.maxByOrNull { it.id }?.id.let {
            if (it == null)
                1
            else
                it + 1
        }

        val newTodo = Todo(newId, title)
        todos.add(newTodo)
        model.addAttribute("todos", todos)

        logger.info { "created todo : $newTodo" }

        return "todo::todo"
    }

    @PostMapping(path = ["/reset"])
    fun reset(model: Model): String {
        publisher.publishEvent(TodoResetEvent())
        model.addAttribute("todos", todos)
        return "todo::todo"
    }
}
