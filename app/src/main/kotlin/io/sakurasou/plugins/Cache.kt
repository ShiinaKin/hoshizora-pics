package io.sakurasou.plugins

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import io.ktor.server.application.*
import kotlin.time.Duration.Companion.seconds

/**
 * @author Shiina Kin
 * 2024/9/11 13:07
 */
fun Application.configureCache(host: String, port: String) {
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }
    // install(SimpleCache) {
    //     redisCache {
    //         invalidateAt = 10.seconds
    //         host = "localhost"
    //         port = 6379
    //     }
    // }
}