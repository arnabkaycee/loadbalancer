package io.github.arnabkaycee.configuration

import kotlinx.serialization.Serializable

@Serializable
data class LoadBalancerConfiguration(val lbInstanceId: String, val services: Set<UpstreamService>)