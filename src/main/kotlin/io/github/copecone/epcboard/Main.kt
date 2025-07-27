package io.github.copecone.epcboard

import io.github.cdimascio.dotenv.dotenv
import io.github.copecone.epcboard.logger.InitLogger
import io.github.copecone.epcboard.util.random.SecureRNG
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val dotenv = dotenv()
val logger: Logger = LoggerFactory.getLogger("MainKt")

internal object Environment {
    val randomOrgAPIKey: String = dotenv["RANDOM_ORG_API_KEY"]
}

fun main() {
    InitLogger.info("Initializing SecureRNG")

    val rng = SecureRNG()
    // rng.pregenerateFloat(10, 0f..100f)

    InitLogger.info("Initializing Embedded Server")
    val server = embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        routing {
            get("/random") {
                call.respondText(rng.getFloat(0f..100f).toString())
            }
        }
    }

    logger.info("Starting Embedded Server")
    server.start(wait = true)
}
