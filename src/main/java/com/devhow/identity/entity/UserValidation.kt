package com.devhow.identity.entity

import com.devhow.identity.user.now
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.sql.Timestamp
import java.util.*

@Entity(name = "user_validation")
@Table(name = "user_validation")
data class UserValidation(
    @Id
    @Column(name = "user_id", nullable = false)
    var user: Long? = null,

    var token: String? = null,
    var tokenIssue: Timestamp? = null,

    @Column(name = "pass_reset_token")
    var passwordResetToken: String? = null,

    @Column(name = "pass_reset_issue")
    var passwordResetIssue: Timestamp? = null,

    @Column(name = "creation")
    @CreationTimestamp
    var creation: Timestamp? = null,

    @Version
    @Column(name = "entity_version", nullable = false)
    var version: Long? = null

) {


    constructor(user: User) : this(user.id, null, null, null, null, null, null)

    fun newToken() {
        token = UUID.randomUUID().toString()
        tokenIssue = Timestamp(Calendar.getInstance().time.time)
    }

    fun tokenIsCurrent(): Boolean {
        return Math.abs(tokenIssue!!.time - now().time) < 1000 * 60 * 60 * 24
    }

    fun passwordValidationIsCurrent(): Boolean {
        return Math.abs(passwordResetIssue!!.time - now().time) < 1000 * 60 * 5
    }

    fun newPasswordResetToken() {
        passwordResetToken = UUID.randomUUID().toString()
        passwordResetIssue = Timestamp(Calendar.getInstance().time.time)
    }
}
