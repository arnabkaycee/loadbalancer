package io.github.arnabkaycee.healthcheck

import io.github.arnabkaycee.persistence.DBInstance
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
//import org.mapdb.SortedTableMap

class LoadBalancerHealthCheckStatusRepository {
    private val logger = KotlinLogging.logger {}

    private val sortedStringTableMap: MutableMap<String, ByteArray> by lazy {
        DBInstance.createSSTable("healthStatus")
    }

    fun loadStatus(lbInstanceId: String, serviceName: String, serviceInstanceId: String): ServiceInstanceHealthStatus? {
        return sortedStringTableMap["$lbInstanceId#$serviceName#$serviceInstanceId"]?.let {
            logger.info { "Found health check status for instance $lbInstanceId for service $serviceName" }
            Json.decodeFromString(it.toString())
        }
    }

    fun saveStatus(serviceInstanceHealthStatus: ServiceInstanceHealthStatus) {
        with(serviceInstanceHealthStatus){
            sortedStringTableMap["$lbInstanceId#$serviceName#$serviceInstanceId"] =
                Json.encodeToString(this).toByteArray()
            logger.info { "Saved service status for $lbInstanceId for service $serviceName" }
        }
    }
}