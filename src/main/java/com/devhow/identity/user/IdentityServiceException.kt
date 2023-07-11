package com.devhow.identity.user

class IdentityServiceException(val reason : Reason,
                               override val message: String? = null) : Exception(message) {

    enum class Reason {
        BAD_EMAIL,
        BAD_LOGIN,
        BAD_PASSWORD,
        BAD_PASSWORD_RESET,
        BAD_TOKEN
    }
}
