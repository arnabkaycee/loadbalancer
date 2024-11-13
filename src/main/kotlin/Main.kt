package io.github.arnabkaycee

import io.github.arnabkaycee.configuration.ConfigLoader
import io.github.arnabkaycee.handler.configureRouting
import io.github.arnabkaycee.handler.configureSerialization
import io.ktor.server.application.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.module() {
    val lbConfig = ConfigLoader.loadConfig()
    configureSerialization()
    configureRouting(lbConfig)
}

fun main(args: Array<String>) {
    logger.info { "Starting load balancer application..." }
    io.ktor.server.netty.EngineMain.main(args)
}
