package ch.frankel.chopshop

fun price(cart: Cart): Double {
    return cart.content.entries
        .fold(0.0) { current, entry ->
            current + entry.key.price * entry.value
        }
}
