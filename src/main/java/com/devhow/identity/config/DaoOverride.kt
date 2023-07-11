package com.devhow.identity.config

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

class DaoOverride : DaoAuthenticationProvider() {

    private lateinit var passwordEncoder: PasswordEncoder

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        return super.authenticate(authentication)
    }

    @Throws(AuthenticationException::class)
    override fun additionalAuthenticationChecks(
        userDetails: UserDetails,
        authentication: UsernamePasswordAuthenticationToken
    ) {
        if (authentication.credentials == null) {
            logger.debug("Failed to authenticate since no credentials provided")
            throw BadCredentialsException(
                messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "No password")
            )
        }
        val presentedPassword = authentication.credentials.toString()
        if (!this.passwordEncoder!!.matches(presentedPassword, userDetails.password)) {
            logger.debug("Failed to authenticate since password does not match stored value")
            throw BadCredentialsException(
                messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials")
            )
        }
        super.additionalAuthenticationChecks(userDetails, authentication)
    }
}
