package io.github.arnabkaycee.router

import io.github.arnabkaycee.configuration.ServiceInstance
import io.github.arnabkaycee.configuration.UpstreamService
import mu.KotlinLogging
import java.net.URI

interface Router {
    private companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun route(inputRequestUri: String): ServiceInstance
    fun upstreamFromPrefix(inputRequestUri: String, services: Collection<UpstreamService>): UpstreamService {
        val uri = URI(inputRequestUri)
        val matchedServices = services.filter { uri.path.startsWith(it.pathPrefix) }
        check(matchedServices.count() == 1) { "Requested url does not match any upstream service prefix, found path: ${uri.path}" }
        val matchedService = matchedServices.single()
        logger.info { "Matched service : ${matchedService.serviceName} with path ${uri.path}" }
        return matchedService
    }

    /**
     * Converts the external URI to that of an upstream service
     */
    fun constructUpstreamUrl(
        requestUri: String,
        upstreamService: UpstreamService,
        upstreamInstanceId: String? = null
    ): String {
        val uri = URI(requestUri)
        val upstreamInstance = upstreamInstanceId
            ?.let { instance -> upstreamService.instances.find { it.serviceInstanceId == instance } }
        val host = upstreamInstance?.host ?: uri.host
        val port = upstreamInstance?.port ?: uri.port
        return URI(
            upstreamService.serviceProtocol.toString().lowercase(),
            uri.userInfo,
            host,
            port,
            uri.path.removePrefix(upstreamService.pathPrefix),
            uri.path,
            uri.fragment
        ).normalize().toString()
    }
}