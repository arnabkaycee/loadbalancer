package io.github.arnabkaycee

import io.github.arnabkaycee.configuration.ConfigLoader
import io.github.arnabkaycee.handler.configureRouting
import io.github.arnabkaycee.handler.configureSerialization
import io.github.arnabkaycee.healthcheck.HealthCheckService
import io.github.arnabkaycee.healthcheck.LoadBalancerHealthCheckStatusRepository
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val loadBalancerConfiguration by lazy {
    ConfigLoader.loadConfig()
}
private val loadBalancerHealthCheckStatusRepository by lazy {
    LoadBalancerHealthCheckStatusRepository()
}

fun Application.module() {
    logger.info { "Configuring Serialization..." }
    configureSerialization()
    logger.info { "Configuring Routing..." }
    configureRouting(loadBalancerConfiguration, loadBalancerHealthCheckStatusRepository)
}

fun main(args: Array<String>) {
    logger.info { "Starting load balancer application..." }
    HealthCheckService(
        loadBalancerConfiguration.lbInstanceId,
        loadBalancerConfiguration,
        CoroutineScope(Dispatchers.Default)
    ).startHealthCheck()
    io.ktor.server.netty.EngineMain.main(args)
}
