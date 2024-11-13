package io.github.arnabkaycee.healthcheck

import io.github.arnabkaycee.configuration.LoadBalancerConfigurationService
import io.github.arnabkaycee.configuration.UpstreamService
import kotlinx.coroutines.*


class HealthCheckService(
    private val lbInstanceId: String,
    private val loadBalancerConfigurationService: LoadBalancerConfigurationService,
    private val coroutineScope: CoroutineScope
) {
    fun startHealthCheck() {
        val loadBalancerConfiguration = loadBalancerConfigurationService.loadConfiguration(lbInstanceId)
        loadBalancerConfiguration?.let { lbConfig ->
            lbConfig.services.forEach { triggerHealthCheckInBackground(it) }
        }
    }

    private fun triggerHealthCheckInBackground(service: UpstreamService) {
        coroutineScope.launch(Dispatchers.Default) {
            while (true) {
                HealthCheckWorkerService(lbInstanceId, service, coroutineScope).check()
                delay(service.healthCheckIntervalMs)
            }
        }
    }
}