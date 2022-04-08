package ch.frankel.chopshop

data class OriginPrice(
    val price: Double,
    val origin: String = "monolith"
)

fun price(cart: Cart) = cart.content.entries
    .fold(0.0) { current, entry ->
        current + entry.key.price * entry.value
    }.toOriginPrice()

private fun Double.toOriginPrice() = OriginPrice(this)
