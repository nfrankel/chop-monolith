package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.renderAndAwait

class HomeHandler(private val catalog: Catalog) {
    suspend fun displayPage(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().renderAndAwait(
            "home", mapOf(
                "products" to catalog.findAll(),
                "cart" to CartQuantityView(req.cart())
            )
        )
}

fun homeRoute(catalog: Catalog) = coRouter {
    val handler = HomeHandler(catalog)
    GET("/", handler::displayPage)
}
