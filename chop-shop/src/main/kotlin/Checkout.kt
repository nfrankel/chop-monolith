package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Suppress("UNUSED")
class CheckoutView(val lines: List<Pair<Product, Int>>) {
    constructor(cart: Cart): this(cart.content.entries.map { it.toPair() })
}

internal fun Cart.toCheckout() = CheckoutView(this)

class CheckoutHandler(private val catalog: Catalog) {

    suspend fun fetchCheckout(req: ServerRequest) =
        ServerResponse.ok().bodyValueAndAwait(req.cart().toCheckout())

    suspend fun removeRow(req: ServerRequest): ServerResponse {
        val cart = req.cart()
        val productId = req.pathVariable("productId")
        val product = catalog.findById(productId.toLong())
        product?.let { cart.remove(product) }
        return ServerResponse.ok().bodyValueAndAwait(req.cart().toCheckout())
    }
}

fun checkoutRoutes(catalog: Catalog) = coRouter {
    val handler = CheckoutHandler(catalog)
    GET("/checkout", handler::fetchCheckout)
    DELETE("/checkout/remove/{productId}", handler::removeRow)
}
