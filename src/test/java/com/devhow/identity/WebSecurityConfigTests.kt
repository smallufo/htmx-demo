package com.devhow.identity

import com.devhow.identity.UserUtils.setupUser
import com.devhow.identity.user.IdentityServiceException
import com.devhow.identity.user.UserService
import jakarta.mail.AuthenticationFailedException
import org.assertj.core.api.Assertions
import org.assertj.core.util.Lists
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["mail.test=true"])
class WebSecurityConfigTests {
    private val restTemplate = TestRestTemplate()
    private val publicURLs =
        arrayOf("/", "/public/sign-in", "/public/forgot-password", "/public/sign-up", "/public/ping")
    private val loginRequiredURLs = arrayOf("/private/")

    @Autowired
    var userService: UserService? = null

    @Autowired
    var passwordEncoder: PasswordEncoder? = null

    @LocalServerPort
    private val port = 0
    private fun createURLWithPort(uri: String): String {
        return "http://localhost:$port$uri"
    }

    @Test
    fun basicAccessChecks() {
        val headers = HttpHeaders()
        val entity = HttpEntity<String?>(null, headers)
        for (publicURL in publicURLs) {
            val response = restTemplate.exchange(
                createURLWithPort(publicURL),
                HttpMethod.GET, entity, String::class.java
            )
            Assertions.assertThat(response.statusCode.value()).isEqualTo(200).describedAs(publicURL)
        }
    }

    @Test
    fun basicLockAccessChecks() {
        val headers = HttpHeaders()
        val entity = HttpEntity<String?>(null, headers)
        for (privateURL in loginRequiredURLs) {
            val response = restTemplate.exchange(
                createURLWithPort(privateURL),
                HttpMethod.GET, entity, String::class.java
            )
            Assertions.assertThat(response.body).contains("Sign In").describedAs(privateURL)
        }
    }

    @Test
    fun apiPing() {
        val headers = HttpHeaders()
        val entity = HttpEntity<String?>(null, headers)
        val response = restTemplate.exchange(
            createURLWithPort("/public/ping"),
            HttpMethod.GET, entity, String::class.java
        )
        Assertions.assertThat(response.statusCode.value()).isEqualTo(200).describedAs("User Service Ping")
        Assertions.assertThat(response.body).contains("OK")
    }

    @Test
    @Throws(AuthenticationFailedException::class, IdentityServiceException::class)
    fun loginTest() {
        val password = "fancy-new-password"
        val (_, username, password1) = setupUser(userService!!, password)
        Assertions.assertThat(passwordEncoder!!.matches(password, password1)).isTrue()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = Lists.list(
            MediaType.TEXT_HTML,
            MediaType.APPLICATION_XHTML_XML
        )
        val parameters: MultiValueMap<String, String?> = LinkedMultiValueMap()
        parameters.add("username", username)
        parameters.add("password", password)
        val request = HttpEntity(parameters, headers)
        val response = restTemplate.postForEntity(
            createURLWithPort("/public/do-sign-in"), request, String::class.java
        )
        Assertions.assertThat(response.statusCode.value()).isEqualTo(302)
        Assertions.assertThat(response.headers["Location"]).doesNotContain("error=true")
    }

    @Test
    @Throws(AuthenticationFailedException::class, IdentityServiceException::class)
    fun badLoginTest() {
        val password = "fancy-new-password"
        val (_, username, password1) = setupUser(userService!!, password)
        Assertions.assertThat(passwordEncoder!!.matches(password, password1)).isTrue()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val parameters: MultiValueMap<String, String?> = LinkedMultiValueMap()
        parameters.add("username", username)
        parameters.add("password", "garbage")
        val request = HttpEntity(parameters, headers)
        val response = restTemplate.postForEntity(
            createURLWithPort("/public/do-sign-in"), request, String::class.java
        )
        Assertions.assertThat(response.statusCode.value()).isEqualTo(302)
        Assertions.assertThat(response.headers["Location"]!![0]).contains("error=true")
    }
}
