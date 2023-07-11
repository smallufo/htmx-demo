package com.devhow.identity

import com.devhow.identity.entity.User
import com.devhow.identity.entity.UserValidation
import com.devhow.identity.user.IdentityServiceException
import com.devhow.identity.user.UserService
import jakarta.mail.AuthenticationFailedException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import java.sql.Timestamp
import java.util.*

@SpringBootTest(properties = ["mail.test=true"])
class UserRegistrationTests {
    private val BCRYPT_TOKEN = "{bcrypt}$2a$"
    private val random = Random()

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private var user = User(true)
    private var userValidation: UserValidation? = null
    @Test
    fun CheckCurrentTokenCalc() {
        Assertions.assertThat(user.validated()).isFalse()
        user.markTokenAsValid()
        Assertions.assertThat(user.validated()).isTrue()
    }

    @Test
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun HappyPathRegistration() {
        val pass = "this-is-just-a-test"
        val username = "wiverson+" + random.nextInt() + "@gmail.com"
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.signIn(
                username,
                pass
            )
        }
        val signedUpUser = userService.signUpUser(username, pass, true)
        Assertions.assertThat(userService.validation(signedUpUser).token).isNotNull()
        Assertions.assertThat(userService.validation(signedUpUser).token).isNotEmpty()
        Assertions.assertThat(signedUpUser.validated()).isFalse()
        val encryptedPass = signedUpUser.password
        Assertions.assertThat(encryptedPass).contains(BCRYPT_TOKEN)
        val confirmUser = userService.confirmUser(userService.validation(signedUpUser).token!!)
        Assertions.assertThat(confirmUser.isPresent).isTrue()
        Assertions.assertThat(confirmUser.get().validated()).isTrue()
        Assertions.assertThat(confirmUser.get().password).isEqualTo(encryptedPass)
        val (_, _, password) = User(user.username, pass, true)
        Assertions.assertThat(password).doesNotContain(BCRYPT_TOKEN)
        val signIn = userService.signIn(username, pass)
        Assertions.assertThat(signIn).isNotNull()
        Assertions.assertThat(signIn.password).contains(BCRYPT_TOKEN)
    }

    /**
     * Existing confirmed user tries to sign up again
     */
    @Test
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun ExistingUserTriesToSignUpAgain() {
        val username = "wiverson+" + random.nextInt() + "@gmail.com"
        val password = "test-this-is-just-for-testing"
        user = userService.signUpUser(username, password, true)
        Assertions.assertThat(user.validated()).isFalse()
        val confirmUser = userService.confirmUser(userService.validation(user).token!!)
        Assertions.assertThat(confirmUser.isPresent).isTrue()
        Assertions.assertThat(confirmUser.get().validated()).isTrue()
        val (id, username1, password1, version, isTest, tokenValidation, creation) = User(
            user.username,
            user.password,
            true
        )

        // This is the key flow here - if a user tries to sign up again but is already confirmed,
        // the returned user will show up as isEnabled.
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.signUpUser(
                username,
                password,
                true
            )
        }
    }

    /**
     * Invalid token path
     */
    @Test
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun InvalidToken() {
        val username = "wiverson+" + random.nextInt() + "@gmail.com"
        val pass = "test-is-just-for-a-test"
        user = userService.signUpUser(username, pass, true)
        Assertions.assertThat(user.validated()).isFalse()
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.confirmUser(
                "garbage token"
            )
        }
        user = userService.findUser(user.username!!).orElseThrow()
        Assertions.assertThat(user.validated()).isFalse()
    }

    /**
     * Invalid email address
     */
    @Test
    fun InvalidEmailAddress() {
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            user = userService.signUpUser("garbage email", "test-this-is-just-for-testing", true)
        }
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            user = userService.signUpUser("", "test-this-is-just-for-testing", true)
        }
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            user = userService.signUpUser("a", "test-this-is-just-for-testing", true)
        }
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            user = userService.signUpUser("a.c", "test-this-is-just-for-testing", true)
        }
    }

    /**
     * Invalid password
     */
    @Test
    fun InvalidPassword() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IdentityServiceException::class.java, { userService.signUpUser("wiverson+test@gmail.com", "", true) },
            "Empty password"
        )
        org.junit.jupiter.api.Assertions.assertThrows(
            IdentityServiceException::class.java, { userService.signUpUser("wiverson+test@gmail.com", "123", true) },
            "Too short password"
        )
        org.junit.jupiter.api.Assertions.assertThrows(
            IdentityServiceException::class.java,
            { userService.signUpUser("wiverson+test@gmail.com", "299 929 2929", true) },
            "Password has spaces"
        )
    }

    /**
     * Expired token
     */
    @Test
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun ExpiredToken() {
        user = userService.signUpUser("wiverson+" + random.nextInt() + "@gmail.com", "test-is-just-for-a-test", true)
        Assertions.assertThat(user.validated()).isFalse()
        val timeInMillis = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMillis
        cal[Calendar.DATE] = cal[Calendar.DATE] - 5
        val userValidation = userService.validation(user)
        userValidation.tokenIssue = Timestamp(cal.timeInMillis)
        userService.update(userValidation)
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.confirmUser(
                userService.validation(user).token!!
            )
        }
        Assertions.assertThat(userService.findUser(user.username!!).orElseThrow().validated()).isFalse()
    }

    @Test
    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun ResetPassword() {
        val startingPassword = "test-is-just-for-a-test"
        val username = "wiverson+" + random.nextInt() + "@gmail.com"

        // password reset is requested but email doesn't exist
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userValidation = userService.requestPasswordReset(
                user.username!!
            )
        }
        user = userService.signUpUser(username, startingPassword, true)

        // password reset is requested but token has not been validated
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.requestPasswordReset(
                user.username!!
            )
        }
        userService.signIn(user.username!!, startingPassword)

        // Confirm user with token
        userService.confirmUser(userService.validation(user).token!!)
        userValidation = userService.requestPasswordReset(user.username!!)
        Assertions.assertThat(userValidation!!.passwordResetIssue).isNotNull()
        Assertions.assertThat(userValidation!!.passwordResetToken).isNotNull()

        // password reset is requested for valid account but password reset token expired
        userService.requestPasswordReset(user.username!!)
        val userValidation = userService.validation(user)
        val newPassword = "this-is-a-fancy-new-password"
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.signIn(
                user.username!!, newPassword
            )
        }
        user = userService.signIn(user.username!!, startingPassword)
        user = userService.updatePassword(user.username!!, userValidation.passwordResetToken!!, newPassword)
        Assertions.assertThat(user.password).contains(BCRYPT_TOKEN)
        org.junit.jupiter.api.Assertions.assertThrows(IdentityServiceException::class.java) {
            userService.signIn(
                user.username!!, startingPassword
            )
        }
        userService.signIn(user.username!!, newPassword)
    }
}
