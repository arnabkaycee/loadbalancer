package io.github.arnabkaycee.configuration

import io.github.arnabkaycee.persistence.DBInstance
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging

class LoadBalancerConfigurationService {

    private companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val sortedStringTableMap: MutableMap<String, ByteArray> by lazy {
        DBInstance.createSSTable("config")
    }

    fun loadConfiguration(lbInstanceId: String): LoadBalancerConfiguration? {
        return sortedStringTableMap[lbInstanceId]?.let {
            logger.info { "Found configuration for instance: $lbInstanceId" }
            Json.decodeFromString(it.toString())
        }
    }
    fun saveConfiguration(loadBalancerConfiguration: LoadBalancerConfiguration){
        sortedStringTableMap[loadBalancerConfiguration.lbInstanceId] = Json.encodeToString(loadBalancerConfiguration).toByteArray()
        logger.info { "Saved configuration for instance: ${loadBalancerConfiguration.lbInstanceId}" }
    }
}