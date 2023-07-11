package com.devhow.identity.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.text.MessageFormat

@Service
class SecurityUserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        return userRepository.findByUsername(email).orElseThrow {
            UsernameNotFoundException(
                MessageFormat.format(
                    "User with email {0} cannot be found.",
                    email
                )
            )
        }
            .securityUser()
    }
}
