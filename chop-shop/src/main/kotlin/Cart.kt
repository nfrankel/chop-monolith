package ch.frankel.chopshop

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.server.*

class Cart {
    val content = sortedMapOf<Product, Int>()

    fun add(product: Product, quantity: Int = 1) {
        content.merge(product, quantity) { current, _ ->
            current + quantity
        }
    }

    fun decrease(product: Product, quantity: Int = 1) {
        content.merge(product, quantity) { current, _ ->
            current - quantity
        }
    }

    fun remove(product: Product) {
        content.remove(product)
    }

    fun quantity() = content.values.sum()
}

class CartQuantityView(cart: Cart) {
    @Suppress("UNUSED")
    val quantity = cart.quantity()
}

suspend fun ServerRequest.cart(): Cart {
    val session = session().awaitSingle()
    val cart = session.getAttributeOrDefault("cart", Cart())
    session.attributes["cart"] = cart
    return cart
}

class CartHandler(private val catalog: Catalog) {
    suspend fun add(req: ServerRequest): ServerResponse {
        val cart = req.cart()
        val productId = req.pathVariable("productId")
        val product = catalog.findById(productId.toLong())
        return if (product == null) ServerResponse.badRequest().buildAndAwait()
        else {
            cart.add(product, 1)
            ServerResponse.ok().bodyValueAndAwait(CartQuantityView(cart))
        }
    }

    suspend fun cartQuantity(req: ServerRequest) =
        ServerResponse.ok().bodyValueAndAwait(CartQuantityView(req.cart()))
}

fun cartRoutes(catalog: Catalog) = coRouter {
    val handler = CartHandler(catalog)
    GET("/cart/quantity", handler::cartQuantity)
    POST("/cart/add/{productId}", handler::add)
}
