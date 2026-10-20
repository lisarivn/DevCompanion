package com.example.devcompanion.feature.devtools.uuid

import java.util.UUID

object UuidGenerator {

    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
