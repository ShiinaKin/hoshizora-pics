package io.sakurasou.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.sakurasou.config.JwtConfig
import io.sakurasou.config.JwtConfig.audience
import io.sakurasou.util.JwtUtils

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(JwtUtils.verifier())
            validate { credential ->
                if (credential.payload.audience.contains(audience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
