package io.sakurasou.plugins

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import com.ucasoft.ktor.simpleRedisCache.redisCache
import io.ktor.server.application.*
import kotlin.time.Duration.Companion.seconds

/**
 * @author Shiina Kin
 * 2024/9/11 13:07
 */
fun Application.configureCache(host: String, port: String) {
    install(SimpleCache) {
        if (host != "disabled") {
            redisCache {
                invalidateAt = 10.seconds
                this.host = host
                this.port = port.toInt()
            }
        } else {
            memoryCache {
                invalidateAt = 10.seconds
            }
        }
    }
}