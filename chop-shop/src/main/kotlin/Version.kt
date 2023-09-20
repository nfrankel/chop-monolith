package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

data class Version(val value: String)

fun versionRoute(version: String) = coRouter {
    GET("/version") { ServerResponse.ok().bodyValueAndAwait(Version(version)) }
}
