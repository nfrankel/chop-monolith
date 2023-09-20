package ch.frankel.chopshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans


@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args) {
        addInitializers(
            beans {
                bean { productRoutes(ref()) }
                bean { homeRoute(ref()) }
                bean { cartRoutes(ref()) }
                bean { checkoutRoutes(ref()) }
                bean { versionRoute(env.getProperty("app.version", "unknown")) }
            }
        )
    }
}
