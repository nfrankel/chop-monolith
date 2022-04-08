package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.*

@Suppress("UNUSED")
class CheckoutView(val cart: Cart, val total: OriginPrice)

class CheckoutHandler(private val catalog: Catalog) {
    suspend fun displayPage(@Suppress("UNUSED_PARAMETER") req: ServerRequest): ServerResponse {
        val cart = req.cart()
        val total = price(cart)
        val view = CheckoutView(cart, total)
        return ServerResponse.ok().renderAndAwait("checkout", mapOf("checkout" to view))
    }

    suspend fun removeRow(req: ServerRequest): ServerResponse {
        val cart = req.cart()
        val productId = req.pathVariable("productId")
        val product = catalog.findById(productId.toLong())
        product?.let { cart.remove(product) }
        return ServerResponse.ok().headers { it.set("Location", "/checkout") }.buildAndAwait()
    }
}

fun checkoutRoutes(catalog: Catalog) = coRouter {
    val handler = CheckoutHandler(catalog)
    GET("/checkout", handler::displayPage)
    DELETE("/checkout/remove/{productId}", handler::removeRow)
}
