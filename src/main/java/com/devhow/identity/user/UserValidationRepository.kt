package com.devhow.identity.user

import com.devhow.identity.entity.UserValidation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserValidationRepository : JpaRepository<UserValidation, Long> {
    fun findByToken(token: String): Optional<UserValidation>
}
