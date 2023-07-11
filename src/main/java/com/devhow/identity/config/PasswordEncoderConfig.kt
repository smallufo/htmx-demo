package com.devhow.identity.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
open class PasswordEncoderConfig {

    @Bean
    @Qualifier("passwordEncoder")
    open fun passwordEncoder(): PasswordEncoder {
        val x = PasswordEncoderFactories.createDelegatingPasswordEncoder() as DelegatingPasswordEncoder
        x.setDefaultPasswordEncoderForMatches(BCryptPasswordEncoder())
        return x
    }
}
