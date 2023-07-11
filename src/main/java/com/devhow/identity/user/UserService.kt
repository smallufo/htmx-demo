package com.devhow.identity.user

import com.devhow.identity.entity.User
import com.devhow.identity.entity.UserValidation
import jakarta.mail.AuthenticationFailedException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userValidationRepository: UserValidationRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailSenderService: EmailSenderService
) {
    private val logger = KotlinLogging.logger { }

    @Value("\${external.server.address}")
    private lateinit var serverAddress: String

    @Value("\${spring.mail.username}")
    private lateinit var emailSender: String
    fun validation(user: User): UserValidation {
        return userValidationRepository.findById(user.id)
            .orElseThrow { IllegalArgumentException("Need to save the user before using validation") }
    }

    private fun sendConfirmationMail(user: User) {
        val mailMessage = SimpleMailMessage()
        mailMessage.setTo(user.username)
        mailMessage.subject = "ChangeNode: Finish Setting Up Your Account"
        mailMessage.from = emailSender
        mailMessage.text = """
            Thank you for registering!
            Please click on the below link to activate your account.
            
            $serverAddress/public/sign-up/confirm?token=${validation(user).token}
            """.trimIndent()
        emailSenderService.sendEmail(mailMessage)
    }

    private fun sendPasswordResetLink(user: User) {
        val mailMessage = SimpleMailMessage()
        mailMessage.setTo(user.username)
        mailMessage.subject = "ChangeNode: Password Reset Link"
        mailMessage.from = emailSender
        mailMessage.text = """
            Here's your password reset link. Only valid for apx two hours.
            Please click on the below link to reset your account password.
            
            $serverAddress/public/password-reset?token=${validation(user).passwordResetToken}
            """.trimIndent()
        emailSenderService.sendEmail(mailMessage)
    }

    @Throws(IdentityServiceException::class)
    private fun checkEmailAddress(address: String) {
        // Really, really basic validation to ensure the email address has an @ symbol that's not at the start or end
        if (!address.contains("@")) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_EMAIL,
            "Invalid email address (1)"
        )
        if (address.endsWith("@")) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_EMAIL,
            "Invalid email address (2)"
        )
        if (address.startsWith("@")) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_EMAIL,
            "Invalid email address (3)"
        )
        if (address.length < 5) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_EMAIL,
            "Invalid email address (4)"
        )
        if (!address.contains(".")) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_EMAIL,
            "Invalid email address (5)"
        )
    }

    @Throws(IdentityServiceException::class)
    private fun checkPassword(password: String?) {
        if (password == null) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "No password set."
        )
        if (password.length < 12) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "Password is too short."
        )
        if (password.length > 200) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "Password is too long."
        )
        if (password.trim { it <= ' ' } != password) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "No spaces in password."
        )
        if (password.contains(" ")) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "No spaces in password."
        )
        val clean = password.replace("[^\\n\\r\\t\\p{Print}]".toRegex(), "")
        if (password != clean) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD,
            "No non-printable characters in password."
        )
    }

    @Throws(IdentityServiceException::class)
    fun signIn(username: String, pass: String): User {
        val user = userRepository.findByUsername(username).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_LOGIN, "Unknown Email"
            )
        }
        if (!passwordEncoder.matches(
                pass,
                user.password
            )
        ) throw IdentityServiceException(IdentityServiceException.Reason.BAD_LOGIN, "Invalid Login (1)")
        return user
    }

    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun signUpUser(username: String, pass: String?, isTest: Boolean): User {

        logger.info { "signUp , username = $username , pass = $pass , isTest = $isTest" }

        // First normalize user email
        checkEmailAddress(username)
        checkPassword(pass)

        // First verify user doesn't already exist
        val foundUser = userRepository.findByUsername(username)
        if (foundUser.isPresent) {
            throw IdentityServiceException(IdentityServiceException.Reason.BAD_EMAIL, "Email already exists.")
        }
        var newUser = User().apply {
            this.username = username.trim { it <= ' ' }.lowercase(Locale.getDefault())
            this.password = passwordEncoder.encode(pass)
            this.isTest = isTest
        }
        require(
            passwordEncoder.matches(
                pass,
                newUser.password
            )
        ) { "The passwordEncoder just failed to match an encoded password!" }
        newUser = userRepository.save(newUser)
        val userValidation = UserValidation(newUser)
        userValidation.newToken()
        userValidationRepository.save(userValidation)
        sendConfirmationMail(newUser)
        return newUser
    }

    fun deleteUser(user: User) {
        require(user.isTest) { "Can only delete test users!" }
        userValidationRepository.deleteById(user.id)
        userRepository.delete(user)
    }

    @Throws(AuthenticationFailedException::class)
    private fun existingUserSignup(user: User): User {
        if (user.tokenValidation != null) return user
        if (validation(user).tokenIsCurrent()) {
            sendConfirmationMail(user)
        } else {
            validation(user).newToken()
            userValidationRepository.save(validation(user))
        }
        return user
    }

    @Throws(IdentityServiceException::class)
    fun confirmUser(confirmationToken: String): Optional<User> {
        val userValidation = userValidationRepository.findByToken(confirmationToken).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_TOKEN, "Invalid Token (21)"
            )
        }
        val user = userRepository.findById(userValidation.user).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_TOKEN, "Invalid Token (22)"
            )
        }
        if (!validation(user).tokenIsCurrent()) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_TOKEN,
            ""
        )
        user.markTokenAsValid()
        val savedUser = userRepository.save(user)
        return Optional.of(savedUser)
    }

    fun findUser(username: String): Optional<User> {
        return userRepository.findByUsername(username)
    }

    fun update(user: User): User {
        return userRepository.save(user)
    }

    fun update(userValidation: UserValidation): UserValidation {
        return userValidationRepository.save(userValidation)
    }

    @Throws(IdentityServiceException::class, AuthenticationFailedException::class)
    fun requestPasswordReset(username: String): UserValidation {
        val user = userRepository.findByUsername(username).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD_RESET, "Missing email address. (a)"
            )
        }
        if (!user.validated()) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_TOKEN,
            "User never activated (should resend activation email)"
        )
        var uv = userValidationRepository.findById(user.id).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD_RESET, "No validation token found. (b)"
            )
        }
        if (uv.passwordResetIssue != null) if (uv.passwordValidationIsCurrent()) {
            return uv
        }
        uv.newPasswordResetToken()
        uv = userValidationRepository.save(uv)
        sendPasswordResetLink(user)
        return uv
    }

    @Throws(IdentityServiceException::class)
    fun updatePassword(username: String, passwordResetToken: String, newPassword: String?): User {
        val user = userRepository.findByUsername(username).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD_RESET, "No user found with this email. (c)"
            )
        }
        val userValidation = userValidationRepository.findById(user.id).orElseThrow {
            IdentityServiceException(
                IdentityServiceException.Reason.BAD_PASSWORD_RESET, "No user validation token[s] found. (d)"
            )
        }
        if (userValidation.passwordResetToken != passwordResetToken) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD_RESET, "Invalid/expired token. (e)"
        )
        if (!userValidation.passwordValidationIsCurrent()) throw IdentityServiceException(
            IdentityServiceException.Reason.BAD_PASSWORD_RESET,
            "Token expired. (f)"
        )
        user.password = passwordEncoder.encode(newPassword)

        // Clear the now no longer useful tokens.
        userValidation.passwordResetIssue = null
        userValidation.passwordResetToken = null
        userValidationRepository.save(userValidation)
        return userRepository.save(user)
    }
}
