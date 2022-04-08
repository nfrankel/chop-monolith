package ch.frankel.chopshop

import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

class Product(
    @Id
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val hero: Boolean
) : Comparable<Product> {
    override fun compareTo(other: Product) = (id - other.id).toInt()
}

interface Catalog : CoroutineCrudRepository<Product, Long>

class ProductHandler(private val catalog: Catalog) {
    suspend fun getAll(@Suppress("UNUSED_PARAMETER") req: ServerRequest) =
        ServerResponse.ok().bodyAndAwait(catalog.findAll())
}

fun productRoutes(catalog: Catalog) = coRouter {
    val handler = ProductHandler(catalog)
    GET("/product", handler::getAll)
}
