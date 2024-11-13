package io.github.arnabkaycee.configuration

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path

class ConfigLoader {

    companion object {
        private val logger = KotlinLogging.logger {}
        fun loadConfig(): LoadBalancerConfiguration {
            val configFile =
                System.getProperty("configFile") ?: throw IllegalStateException("Cannot find config file...")
            val isReadable = Files.isReadable(Path.of(configFile))
            val file = Path.of(configFile).toAbsolutePath().toFile()
            logger.info { "Loading configuration from file : ${file.absoluteFile}, readable: $isReadable" }
            return ConfigLoaderBuilder
                .default()
                .addFileSource(file)
                .strict()
                .build()
                .loadConfigOrThrow<LoadBalancerConfiguration>()
        }
    }
}