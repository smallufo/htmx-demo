package com.devhow.identity.config

import com.devhow.identity.user.SecurityUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
open class WebSecurityConfig(
    private val userService: SecurityUserService,
    private val passwordEncoder: PasswordEncoder
) {
    @Value("spring.profiles.active")
    private lateinit var activeProfile: String

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // @formatter:off
    http.csrf().disable()
        
                // Only allow frames if using h2 as the database
        if (activeProfile.contains("h2"))http.headers().frameOptions().disable()
        http.authorizeHttpRequests()
        .requestMatchers("/**/*.html").denyAll()
        .requestMatchers("/public/**", "/webjars/**", "/", "/logout", "/api/**", "/login", "/h2-console/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage("/public/sign-in").permitAll()
        .loginProcessingUrl("/public/do-sign-in") //                    .defaultSuccessUrl("/")
        .failureUrl("/public/sign-in?error=true")
        .usernameParameter("username")
        .passwordParameter("password") //.failureHandler(userService)
 //.successHandler(userService)
        .and()
        .logout()
        .logoutRequestMatcher(AntPathRequestMatcher("/public/logout"))
        .clearAuthentication(true)
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        return http.build()
    
//                .and()
//                    .anonymous();
        // @formatter:on
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder)
    }

    @Bean
    open fun authProvider(): AuthenticationProvider {
        val authProvider: DaoAuthenticationProvider = DaoOverride()
        authProvider.setUserDetailsService(userService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }
}
