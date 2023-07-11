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
class User {
    @JvmField
    @get:Column(name = "pass")
    var password: String? = null
    @JvmField
    var username: String? = null

    @get:Column(name = "entity_version", nullable = false)
    @get:Version
    var version: Long? = null

    @JvmField
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Id
    @Id
    var id: Long? = null
    var isTest = false
    var tokenValidation: Timestamp? = null

    @get:CreationTimestamp
    var creation: Timestamp? = null

    constructor()
    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }

    constructor(username: String?, password: String?, test: Boolean) {
        this.username = username
        this.password = password
        isTest = test
    }

    constructor(test: Boolean) {
        isTest = test
    }

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

    override fun toString(): String {
        return "User{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", version=" + version +
                ", id=" + id +
                ", test=" + isTest +
                ", tokenValidation=" + tokenValidation +
                ", creation=" + creation +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val user = o as User
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
