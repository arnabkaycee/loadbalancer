package io.github.arnabkaycee.configuration

import kotlinx.serialization.Serializable

@Serializable
data class ServiceInstance(
    val serviceInstanceId: String,
    val host: String,
    val port: Int?,
    val healthCheckPort: Int? = port
)
