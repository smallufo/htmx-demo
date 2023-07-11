package com.devhow.identity.user

import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class EmailSenderService(private val javaMailSender: JavaMailSender) {

    private val logger = KotlinLogging.logger { }

    @Value("\${mail.test:false}")
    private var mailTest = false

    @Async
    open fun sendEmail(email: SimpleMailMessage) {
        if (mailTest) {
            logger.error(email.text)
        } else {
            try {
                javaMailSender.send(email)
            } catch (e: Exception) {
                logger.error("Unable to send email! Future emails will be logged - no retry!", e)
                mailTest = true
                logger.error(email.text)
            }
        }
    }
}
