package com.devhow.identity.entity

import com.devhow.identity.user.now
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.sql.Timestamp
import java.util.*

@Entity(name = "users")
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var username: String? = null,

    @Column(name = "pass")
    var password: String? = null,

    @Column(name = "entity_version", nullable = false)
    @Version
    var version: Long? = null,

    var isTest: Boolean = false,

    var tokenValidation: Timestamp? = null,

    @CreationTimestamp
    var creation: Timestamp? = null
) {

    fun securityUser(): org.springframework.security.core.userdetails.User {
        val grantedAuthorities: MutableList<SimpleGrantedAuthority> = ArrayList()
        grantedAuthorities.add(SimpleGrantedAuthority(id.toString()))
        grantedAuthorities.add(SimpleGrantedAuthority("USER"))
        return org.springframework.security.core.userdetails.User(username, password, grantedAuthorities)
    }

    fun validated(): Boolean {
        return tokenValidation != null
    }

    fun markTokenAsValid() {
        tokenValidation = now()
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val user = other as User
        return isTest == user.isTest && password == user.password && username == user.username && version == user.version && id == user.id && tokenValidation == user.tokenValidation && creation == user.creation
    }

    override fun hashCode(): Int {
        return Objects.hash(password, username, version, id, isTest, tokenValidation, creation)
    }

    companion object {
        fun id(authentication: Authentication): Long {
            return authentication.authorities.toTypedArray()[0].toString().toLong()
        }
    }
}
