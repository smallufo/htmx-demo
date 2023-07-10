package com.devhow.htmxdemo

import com.github.javafaker.Faker

data class Contact(var firstName: String?, var lastName: String?, var email: String?) {


    companion object {
        fun demoContact(): Contact {
            return Contact("Bob", "Smith", "bsmith@example.com")
        }

        private val faker = Faker()
        fun randomContacts(count: Int): List<Contact> {
            val result: MutableList<Contact> = ArrayList()

            for (i in 0 until count) {
                val contact = Contact(
                    faker.name().firstName(),
                    faker.name().firstName(),
                    faker.internet().safeEmailAddress()
                )
                result.add(contact)
            }
            return result
        }
    }
}
