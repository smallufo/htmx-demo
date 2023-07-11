package com.devhow.identity

import com.devhow.identity.entity.User
import com.devhow.identity.user.IdentityServiceException
import com.devhow.identity.user.UserService
import jakarta.mail.AuthenticationFailedException
import java.util.*

object UserUtils {
    private val random = Random()
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun setupUser(userService: UserService): User {
        val pass = "this-is-just-a-test"
        return setupUser(userService, pass)
    }

    @JvmStatic
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun setupUser(userService: UserService, pass: String?): User {
        val user = userService.signUpUser("wiverson+" + random.nextInt() + "@gmail.com", pass, true)
        val confirmUser = userService.confirmUser(userService.validation(user).token!!)
        return userService.signIn(confirmUser.orElseThrow().username!!, pass!!)
    }
}
