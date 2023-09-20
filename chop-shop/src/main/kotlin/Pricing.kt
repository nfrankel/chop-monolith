package ch.frankel.chopshop

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.server.*

data class CompleteView(private val cart: CheckoutView, val total: OriginPrice) {
    @Suppress("UNUSED")
    val lines: List<Pair<Product, Int>>
        get() = cart.lines
}

data class OriginPrice(
    val price: Double,
    val origin: String = "monolith"
)

private fun Double.toOriginPrice() = OriginPrice(this)

fun price(checkout: CheckoutView) = checkout.lines
    .fold(0.0) { current, line ->
        current + line.first.price * line.second
    }.toOriginPrice()

class PricingHandler {
    suspend fun compute(req: ServerRequest): ServerResponse {
        val cart = req.bodyToMono<CheckoutView>().awaitSingle()
        val price = price(cart)
        return ServerResponse.ok().bodyValueAndAwait(CompleteView(cart, price))
    }
}

fun pricingRoute() = coRouter {
    val handler = PricingHandler()
    POST("/price", handler::compute)
}
