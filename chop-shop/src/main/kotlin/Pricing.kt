package ch.frankel.chopshop

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.server.*

fun price(checkout: CheckoutView): Double {
    println("Pricing computed from the monolith")
    return checkout.lines
        .fold(0.0) { current, line ->
            current + line.first.price * line.second
        }
}

class PricingHandler {
    suspend fun compute(req: ServerRequest): ServerResponse {
        val cart = req.bodyToMono<CheckoutView>().awaitSingle()
        val price = price(cart)
        return ServerResponse.ok().bodyValueAndAwait(price)
    }
}

fun pricingRoute() = coRouter {
    val handler = PricingHandler()
    POST("/price", handler::compute)
}
