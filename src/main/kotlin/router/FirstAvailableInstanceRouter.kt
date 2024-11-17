package io.github.arnabkaycee.router

import io.github.arnabkaycee.configuration.LoadBalancerConfiguration
import io.github.arnabkaycee.configuration.ServiceInstance
import io.github.arnabkaycee.healthcheck.LoadBalancerHealthCheckStatusRepository

/**
 * Routes to the first available upstream instance if found,
 * else returns the first instance
 */
class FirstAvailableInstanceRouter(
    private val loadBalancerInstanceId: String,
    private val loadBalancerConfiguration: LoadBalancerConfiguration,
    private val loadBalancerHealthCheckRepository: LoadBalancerHealthCheckStatusRepository
) : Router {
    override fun route(inputRequestUri: String): ServiceInstance {
        val service = upstreamFromPrefix(inputRequestUri, loadBalancerConfiguration.services)
        // route to the first instance that is available
        val foundServiceInstance = service.instances.firstOrNull {
            val instanceHealthStatus = loadBalancerHealthCheckRepository.loadStatus(
                loadBalancerInstanceId, service.serviceName, it.serviceInstanceId
            )
            instanceHealthStatus?.failuresSinceLastAlive == 0
        }
        return foundServiceInstance ?: service.instances.first()
    }
}