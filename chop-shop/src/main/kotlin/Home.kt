package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.*

class HomeHandler(private val catalog: Catalog) {
    suspend fun displayPage(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().renderAndAwait("home")

    suspend fun fetchCatalog(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().bodyAndAwait(catalog.findAll())
}

fun homeRoute(catalog: Catalog) = coRouter {
    val handler = HomeHandler(catalog)
    GET("/", handler::displayPage)
    GET("/catalog", handler::fetchCatalog)
}
