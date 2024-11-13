package io.github.arnabkaycee.handler

import io.github.arnabkaycee.configuration.LoadBalancerConfiguration
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(loadBalancerConfiguration: LoadBalancerConfiguration) {
    routing {
        loadBalancerConfiguration.services.forEach {
            route(it.pathPrefix) {
                handle {
                    ForwardHandler(it).handle(call)
                }
            }
        }
    }
}