package com.devhow.identity.user

import com.devhow.identity.entity.User
import jakarta.mail.AuthenticationFailedException
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.util.*

@Controller
@RequestMapping("/public")
class UserController(private val userService: UserService) {

    private val logger = KotlinLogging.logger { }

    @RequestMapping(value = ["/ping"], produces = ["text/plain"])
    @ResponseBody
    fun ping(@RequestParam(name = "debug", required = false) debug: String?): String {
        return if (debug != null && debug.length > 0) {
            "OK " + Date() + " " + debug
        } else "OK " + Date()
    }

    @RequestMapping(path = ["/logout"])
    fun logout(response: HttpServletResponse): RedirectView {
        response.addHeader("HX-Redirect", "/")
        return RedirectView("/?message=logout")
    }

    @PostMapping("/do-sign-in")
    fun doSignIn(@RequestParam(name = "error", defaultValue = "") error: String, modelMap: ModelMap): String {
        if (error.length > 0) modelMap.addAttribute(ERROR, error)
        return "index"
    }

    @GetMapping("/sign-in")
    fun signIn(
        @RequestParam(name = "error", defaultValue = "") error: String,
        @RequestParam(name = "message", defaultValue = "") message: String, modelMap: ModelMap,
        response: HttpServletResponse
    ): String {
        if (message.length > 0) modelMap.addAttribute(MESSAGE, message)
        if (error.length > 0) modelMap.addAttribute(ERROR, "Invalid Login")
        response.addHeader("HX-Redirect", "/")
        return "identity/sign-in"
    }

    @GetMapping("/password-reset")
    fun passwordResetKey(@RequestParam(name = "token") key: String, modelMap: ModelMap): String {
        modelMap["key"] = key
        return "identity/password-reset"
    }

    @PostMapping("/password-reset")
    fun updatePassword(
        @RequestParam(name = "key") key: String,
        @RequestParam(name = "email") email: String,
        @RequestParam(name = "password1") password1: String,
        @RequestParam(name = "password2") password2: String, modelMap: ModelMap,
        response: HttpServletResponse
    ): String {
        try {
            if (password1.compareTo(password2) != 0) throw IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD_RESET,
                "Passwords don't match"
            )
            userService.updatePassword(email, key, password1)
            return signIn("", "Password successfully updated.", modelMap, response)
        } catch (e: IdentityServiceException) {
            modelMap.addAttribute(MESSAGE, e.message)
        }
        return signIn("", "", modelMap, response)
    }

    @GetMapping("/forgot-password")
    fun forgotPassword(): String {
        return "identity/forgot-password"
    }

    @PostMapping("/forgot-password")
    fun resetPassword(
        @RequestParam(name = "email", defaultValue = "") email: String,
        modelMap: ModelMap, response: HttpServletResponse
    ): String {
        try {
            userService.requestPasswordReset(email)
            return signIn("", "Check your email for password reset link.", modelMap, response)
        } catch (e: IdentityServiceException) {
            if (e.reason == IdentityServiceException.Reason.BAD_TOKEN) modelMap.addAttribute(
                MESSAGE,
                "Unknown Token"
            ) else modelMap.addAttribute(
                MESSAGE, e.message
            )
        } catch (authenticationFailedException: AuthenticationFailedException) {
            modelMap.addAttribute(MESSAGE, "Unable to send email right now...")
        }
        return "identity/forgot-password"
    }

    @GetMapping("/sign-up")
    fun signUpPage(user: User?): String {
        return "identity/sign-up"
    }

    @PostMapping("/sign-up")
    fun signUp(user: User, @RequestParam(name = "password-confirm") confirm: String, modelMap: ModelMap): String {
        try {
            logger.info { "user.password = ${user.password} , confirm = $confirm" }
            if (user.password != confirm) throw IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD,
                "Passwords do not match"
            )
            userService.signUpUser(user.username!!, user.password, false)
            return "redirect:/public/sign-in?message=Check%20your%20email%20to%20confirm%20your%20account%21"
        } catch (e: IdentityServiceException) {
            modelMap.addAttribute(ERROR, e.message)
        } catch (authenticationFailedException: AuthenticationFailedException) {
            modelMap.addAttribute(ERROR, "Can't send email - email server is down/unreachable.")
            authenticationFailedException.printStackTrace()
        }
        return "identity/sign-up"
    }

    @GetMapping("/sign-up/confirm")
    fun confirmMail(@RequestParam("token") token: String, modelMap: ModelMap, response: HttpServletResponse): String {
        return try {
            userService.confirmUser(token)
                .orElseThrow { IdentityServiceException(IdentityServiceException.Reason.BAD_TOKEN, null) }
            signIn("", "Email Address Confirmed!", modelMap, response)
        } catch (e: IdentityServiceException) {
            signIn("", "Unknown Token", modelMap, response)
        }
    }

    companion object {
        const val MESSAGE = "message"
        const val ERROR = "error"
    }
}
