package io.github.arnabkaycee.healthcheck

import io.github.arnabkaycee.configuration.UpstreamService
import kotlinx.coroutines.*
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.Instant


class HealthCheckWorkerService(
    private val lbInstanceId: String,
    private val service: UpstreamService,
    private val coroutineScope: CoroutineScope

) {
    private companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val client: OkHttpClient by lazy {
        val timeout = Duration.ofMillis(service.healthCheckTimeoutMs)
        OkHttpClient
            .Builder()
            .connectTimeout(timeout)
            .writeTimeout(timeout)
            .callTimeout(timeout)
            .build()
    }
    private val healthCheckStatusRepository = LoadBalancerHealthCheckStatusRepository()

    fun check() {
        service
            .healthCheckUrls
            .map { (instanceId, healthCheckUrl) -> instanceId to Request.Builder().url(healthCheckUrl).build() }
            .forEach { (instanceId, healthCheckRequest) ->
                coroutineScope.launch {
                    logger.info { "Checking health for service ${service.serviceName}..." }
                    client.newCall(healthCheckRequest).execute().use { response ->
                        if (response.code == 200) {
                            healthCheckStatusRepository.saveStatus(
                                ServiceInstanceHealthStatus(
                                    lbInstanceId,
                                    service.serviceName,
                                    instanceId,
                                    lastAlive = Instant.now()
                                )
                            )
                            logger.info { "Service ${service.serviceName} is alive..." }
                        } else {
                            val lastHealthCheck = healthCheckStatusRepository.loadStatus(
                                lbInstanceId,
                                service.serviceName,
                                instanceId
                            )
                            val newStatus =
                                lastHealthCheck?.withCurrentStatusAsFailure() ?: ServiceInstanceHealthStatus(
                                    lbInstanceId,
                                    service.serviceName,
                                    instanceId,
                                    failuresSinceLastAlive = 1
                                )
                            healthCheckStatusRepository.saveStatus(newStatus)
                            logger.info { "Service ${service.serviceName} is dead..." }
                        }
                    }
                }
            }
    }
}