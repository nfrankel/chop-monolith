package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

class HomeHandler(private val catalog: Catalog) {

    suspend fun fetchCatalog(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().bodyAndAwait(catalog.findAll())
}

fun homeRoute(catalog: Catalog) = coRouter {
    val handler = HomeHandler(catalog)
    GET("/catalog", handler::fetchCatalog)
}
