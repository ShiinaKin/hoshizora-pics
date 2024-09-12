package io.sakurasou.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import java.util.concurrent.*

/**
 * @author Shiina Kin
 * 2024/9/12 12:44
 */
fun Application.configureJwt() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    JwtConfig.init(jwtSecret, jwtIssuer, jwtAudience, jwtRealm)
}

object JwtConfig {
    lateinit var secret: String
    lateinit var issuer: String
    lateinit var audience: String
    lateinit var realm: String
    lateinit var jwkProvider: JwkProvider

    fun init(jwtSecret: String, jwtIssuer: String, jwtAudience: String, jwtRealm: String) {
        this.secret = jwtSecret
        this.issuer = jwtIssuer
        this.audience = jwtAudience
        this.realm = jwtRealm
        this.jwkProvider = JwkProviderBuilder(jwtIssuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
    }
}