package io.github.arnabkaycee.handler

import io.github.arnabkaycee.configuration.UpstreamService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.time.Duration

class ForwardHandler(private val service: UpstreamService) {
    private val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                    callTimeout(Duration.ofMillis(service.connectionTimeoutMs))
                }
            }
        }
    }

    suspend fun handle(call: RoutingCall) {
        val response = client.request(call.request.uri) {
            method = call.request.httpMethod
            setBody(call.receiveText())
            parametersOf(call.parameters.toMap())
            buildHeaders{
                call.request.headers
            }
        }
        call.respondBytes { response.readRawBytes() }
    }
}