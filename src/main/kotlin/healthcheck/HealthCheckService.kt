package io.github.arnabkaycee.healthcheck

import io.github.arnabkaycee.configuration.LoadBalancerConfiguration
import io.github.arnabkaycee.configuration.UpstreamService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging


class HealthCheckService(
    private val lbInstanceId: String,
    private val loadBalancerConfiguration: LoadBalancerConfiguration,
    private val coroutineScope: CoroutineScope
) {
    private companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun startHealthCheck() {
        loadBalancerConfiguration.let { lbConfig ->
            lbConfig.services.forEach {
                logger.info { "Starting health check for service ${it.serviceName}" }
                triggerHealthCheckInBackground(it)
            }
        }
    }

    private fun triggerHealthCheckInBackground(service: UpstreamService) {
        coroutineScope.launch {
            while (true) {
                HealthCheckWorkerService(lbInstanceId, service, coroutineScope).check()
                delay(service.healthCheckIntervalMs)
            }
        }
    }
}