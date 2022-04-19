package ch.frankel.chopshop

import org.springframework.web.reactive.function.server.*

@Suppress("UNUSED")
class CheckoutView(private val cart: Cart, val total: OriginPrice) {
    val lines: List<Pair<Product, Int>>
        get() = cart.content.entries.map { it.toPair() }
}

internal fun Cart.toCheckout() = CheckoutView(this, price(this))

class CheckoutHandler(private val catalog: Catalog) {
    suspend fun displayPage(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().renderAndAwait("checkout")

    suspend fun fetchCheckout(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
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
    GET("/checkout", handler::displayPage)
    GET("/checkout/c", handler::fetchCheckout)
    DELETE("/checkout/remove/{productId}", handler::removeRow)
}
