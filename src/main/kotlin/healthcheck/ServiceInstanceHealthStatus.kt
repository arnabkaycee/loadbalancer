package io.github.arnabkaycee.healthcheck

import java.time.Instant

data class ServiceInstanceHealthStatus(
    val lbInstanceId: String,
    val serviceName: String,
    val serviceInstanceId: String,
    val lastAlive: Instant? = null,
    val lastPolled: Instant = Instant.now(),
    val failuresSinceLastAlive: Int = 0
) {
    fun withCurrentStatusAsFailure(): ServiceInstanceHealthStatus {
        return copy(lastPolled = Instant.now(), failuresSinceLastAlive = this.failuresSinceLastAlive + 1)
    }
}