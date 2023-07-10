package com.devhow

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableCaching
@EnableAsync
@ConfigurationPropertiesScan
open class HtmxDemoApplication {
    @Bean
    open fun layoutDialect(): LayoutDialect {
        return LayoutDialect()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(HtmxDemoApplication::class.java, *args)
}
