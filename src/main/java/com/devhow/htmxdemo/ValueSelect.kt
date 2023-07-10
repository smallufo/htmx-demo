package com.devhow.htmxdemo

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.intellij.lang.annotations.Language
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.util.*

@Controller
@RequestMapping("/public/value-select")
class ValueSelect : AbstractController() {
    /**
     * IntelliJ has a plugin that supports handlebars inline (and file template) syntax highlighting.
     *
     *
     * https://plugins.jetbrains.com/plugin/6884-handlebars-mustache
     */
    @Language("handlebars")
    private val handleBarTemplate = """
                    {{#each}}
                    <option value="{{this}}">{{{this}}}</option>
                    {{/each}}
                    
                    """.trimIndent()
    private val java8 = arrayOf("lambdas", "collections", "streams")
    private val java9 = arrayOf("collections", "streams", "optionals", "interfaces", "jshell")
    private val java10 = arrayOf("var")
    private val java11 = arrayOf("strings", "scripts", "lambda var")
    private val java12 = arrayOf("unicode 11")
    private val java13 = arrayOf("unicode 12")
    private val java14 = arrayOf("switch", "better null pointer error messages")
    private val java15 = arrayOf("text blocks", "Z garbage collector")
    private val java16 = arrayOf("sockets", "records")
    private val java17 = arrayOf("pattern matching for switch", "sealed classes", "foreign function and memory api")
    private val java18 = arrayOf("UTF-8 by default", "jwebserver")
    private val java19 = arrayOf("virtual threads", "structured concurrency", "vector api")
    private val java20 = arrayOf("scoped values", "record patterns")
    var template: Template? = null

    init {
        val handlebars = Handlebars()
        try {
            template = handlebars.compileInline(handleBarTemplate)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @GetMapping
    fun start(model: Model): String {
        return "value-select"
    }

    @GetMapping(value = ["/models"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun models(@RequestParam("make") make: String): String {
        if ("java8" == make) return template!!.apply(java8)
        if ("java9" == make) return template!!.apply(java9)
        if ("java10" == make) return template!!.apply(java10)
        if ("java11" == make) return template!!.apply(java11)
        if ("java12" == make) return template!!.apply(java12)
        if ("java13" == make) return template!!.apply(java13)
        if ("java14" == make) return template!!.apply(java14)
        if ("java15" == make) return template!!.apply(java15)
        if ("java16" == make) return template!!.apply(java16)
        if ("java17" == make) return template!!.apply(java17)
        if ("java18" == make) return template!!.apply(java18)
        if ("java19" == make) return template!!.apply(java19)
        if ("java20" == make) return template!!.apply(java20)
        throw IllegalArgumentException("Unknown make")
    }
}
