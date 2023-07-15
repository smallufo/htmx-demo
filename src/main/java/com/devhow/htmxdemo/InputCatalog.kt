package com.devhow.htmxdemo

import j2html.TagCreator
import j2html.tags.ContainerTag
import j2html.tags.specialized.PTag
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.awt.Color
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Controller
@RequestMapping("/public/input")
class InputCatalog : AbstractController() {

    @GetMapping
    fun start(model: Model): String {
        return "input-catalog"
    }

    @DeleteMapping(path = ["/delete"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun delete(): String {
        return ""
    }

    @PostMapping(path = ["/button"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun button(@RequestParam("demo-button") button: String): String {
        return TagCreator.p("Button $button clicked.").render()
    }

    @PostMapping(path = ["/checkbox"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun checkbox(@RequestParam parameters: Map<String?, String>): String {
        if (parameters.containsKey("checkbox")) if (parameters.containsKey(parameters["checkbox"])) return TagCreator.p(
            "Checkbox " + parameters["checkbox"] + " checked."
        ).render()
        return TagCreator.p("Checkbox " + parameters["checkbox"] + " unchecked.").render()
    }

    @PostMapping(path = ["/radio"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun radio(@RequestParam("demo-radio") selection: String): String {
        return TagCreator.p("Radio $selection selected.").render()
    }

    @PostMapping(path = ["/slider"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun slider(@RequestParam("demo-range") selection: Int): String {
        return TagCreator.p("Slider $selection value.").render()
    }

    @PostMapping(path = ["/select-single"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun selectSingle(@RequestParam("demo-select-single") selection: String): String {
        return TagCreator.p("Selected $selection.").render()
    }

    @PostMapping(path = ["/select-multiple"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun selectMultiple(@RequestParam("demo-select-multiple") selection: Array<String>): String {
        val p: ContainerTag<PTag> = TagCreator.p("Selected")
        for (s in selection) p.with(TagCreator.span(" $s"))
        return p.render()
    }

    @PostMapping(path = ["/date"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun date(@RequestParam("demo-date") date: String?): String {
        val parse = LocalDate.parse(date)
        return TagCreator.p("Selected $parse.").render()
    }

    @PostMapping(path = ["/time"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun time(@RequestParam("demo-time") time: String?): String {
        val parse = LocalTime.parse(time)
        return TagCreator.p("Selected $parse.").render()
    }

    @PostMapping(path = ["/datetime"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun datetime(@RequestParam("demo-date-time-local") datetime: String?): String {
        val parse = LocalDateTime.parse(datetime)
        return TagCreator.p("Selected $parse.").render()
    }

    @PostMapping(path = ["/color"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun color(@RequestParam("demo-color") color: String?): String {
        val c = Color.decode(color)
        return TagCreator.p(
            TagCreator.join(
                "Hex:",
                color,
                " RGB:",
                c.red.toString() + "",
                c.green.toString() + "",
                c.blue.toString() + ""
            )
        ).render()
    }

    @PostMapping(path = ["/number"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun color(@RequestParam("demo-number") num: Int): String {
        return TagCreator.p("Number: $num").render()
    }

    @PostMapping(path = ["/text"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun text(
        @RequestHeader("HX-Trigger-Name") trigger: String,
        @RequestParam parameters: Map<String?, String?>
    ): String {
        val target = parameters[trigger]
        return if (!parameters.containsKey(trigger)) "" else TagCreator.p("$trigger set to $target").render()
    }

    @PostMapping(path = ["/file"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun file(
        @RequestParam("demo-file") file: MultipartFile,
        @RequestParam parameters: Map<String?, String?>?
    ): String {
        val p: ContainerTag<PTag> = TagCreator.p("File uploaded! ").with(
            TagCreator.join(
                TagCreator.br(),
                " File name: " + file.name, TagCreator.br(),
                " File length: " + file.size + " bytes", TagCreator.br(),
                " File type: " + file.contentType, TagCreator.br(),
                " Original file name: " + file.originalFilename
            )
        )
        return p.render()
    }

    @PostMapping(path = ["/reset"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun reset(): String {
        return TagCreator.p("Form reset!").render()
    }

    @PostMapping(path = ["/submit"], produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun submit(@RequestParam parameters: Map<String, String>): String {
        val p = TagCreator.p("Form submitted!")
        for (s in parameters.keys) p.with(TagCreator.join(TagCreator.br(), s + ":" + parameters[s]))
        return p.render()
    }

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
