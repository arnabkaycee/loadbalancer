package io.github.arnabkaycee.persistence

import mu.KotlinLogging


class DBInstance private constructor(
    val sortedStringTable: MutableMap<String, ByteArray>
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        fun createSSTable(dbName: String) = mutableMapOf<String, ByteArray>()
    }
}