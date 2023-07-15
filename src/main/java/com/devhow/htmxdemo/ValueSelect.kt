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


    private val featureMap = mapOf(
        "java8" to arrayOf("lambdas", "collections", "streams"),
        "java9" to arrayOf("collections", "streams", "optionals", "interfaces", "jshell"),
        "java10" to arrayOf("var"),
        "java11" to arrayOf("strings", "scripts", "lambda var"),
        "java12" to arrayOf("unicode 11"),
        "java13" to arrayOf("unicode 12"),
        "java14" to arrayOf("switch", "better null pointer error messages"),
        "java15" to arrayOf("text blocks", "Z garbage collector"),
        "java16" to arrayOf("sockets", "records"),
        "java17" to arrayOf("pattern matching for switch", "sealed classes", "foreign function and memory api"),
        "java18" to arrayOf("UTF-8 by default", "jwebserver"),
        "java19" to arrayOf("virtual threads", "structured concurrency", "vector api"),
        "java20" to arrayOf("scoped values", "record patterns")
    )

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

    private val template: Template by lazy {
        val handlebars = Handlebars()
        handlebars.compileInline(handleBarTemplate)
    }


    @GetMapping
    fun start(model: Model): String {
        return "value-select"
    }

    @GetMapping(value = ["/models"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun models(@RequestParam("make") make: String): String {

        val arrays = featureMap.getOrElse(make) {
            throw IllegalArgumentException("Unknown make")
        }

        return template.apply(arrays)
    }
}
