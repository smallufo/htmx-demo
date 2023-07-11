package com.devhow.identity.user

import java.sql.Timestamp
import java.util.*

class TimeUtil {
    fun now(): Timestamp {
        return Timestamp(Calendar.getInstance().time.time)
    }
}
