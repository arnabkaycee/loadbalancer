package io.github.arnabkaycee.handler

import io.github.arnabkaycee.configuration.LoadBalancerConfiguration
import io.github.arnabkaycee.healthcheck.LoadBalancerHealthCheckStatusRepository
import io.github.arnabkaycee.router.FirstAvailableInstanceRouter
import io.ktor.server.application.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureRouting(
    loadBalancerConfiguration: LoadBalancerConfiguration,
    loadBalancerHealthCheckStatusRepository: LoadBalancerHealthCheckStatusRepository
) {
    routing {
        loadBalancerConfiguration.services.forEach {
            logger.info { "Configuring load balancing for service ${it.serviceName} with path ${it.pathPrefix}" }
            route(it.pathPrefix) {
                handle {
                    ForwardHandler(
                        it,
                        FirstAvailableInstanceRouter(
                            loadBalancerConfiguration.lbInstanceId,
                            loadBalancerConfiguration,
                            loadBalancerHealthCheckStatusRepository
                        )
                    ).handle(call)
                }
            }
        }
    }
}