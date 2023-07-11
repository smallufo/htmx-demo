package com.devhow.identity

import com.devhow.identity.user.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserDataAccessTests {
    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun dataAccessTest() {
        Assertions.assertThat(userRepository.count()).isGreaterThan(-1)
    }
}
