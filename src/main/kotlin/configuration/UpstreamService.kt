package io.github.arnabkaycee.configuration

import kotlinx.serialization.Serializable

@Serializable
data class UpstreamService(
    val serviceName: String,
    val instances: Set<ServiceInstance>,
    val servicePort: Int,
    val pathPrefix: String,
    val serviceProtocol: Protocol,
    val healthCheckEndpoint: String? = "/health",
    val healthCheckPort: Int? = servicePort,
    val healthCheckTimeoutMs: Long = 30000, // 30s timeout
    val healthCheckIntervalMs: Long,
    val connectionTimeoutMs: Long
) {
    val healthCheckUrls get() = instances.map {
        it.serviceInstanceId to "$serviceProtocol://${it.host}:${it.healthCheckPort}/$healthCheckEndpoint".lowercase()
    }
    val endpointUrls get() = instances.map { "$serviceProtocol://${it.host}:$servicePort".lowercase() }

    fun addInstance(serviceInstance: ServiceInstance): UpstreamService {
        val services = mutableSetOf<ServiceInstance>()
        services.addAll(this.instances)
        services.add(serviceInstance)
        return this.copy(instances = services)
    }
}