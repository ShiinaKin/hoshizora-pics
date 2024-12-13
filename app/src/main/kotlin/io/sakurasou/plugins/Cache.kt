package io.sakurasou.plugins

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.coroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

/**
 * @author Shiina Kin
 * 2024/9/11 13:07
 */
fun Application.configureCache(host: String, port: String) {
    install(Cache) {
        if (host != "disabled") redisProvider(host, port.toInt(), expireTime = 30.minutes)
        else memoryProvider(15.minutes)
    }
}

private val logger = KotlinLogging.logger { }

class CacheConfig(
    var provider: CacheProvider = MemoryCacheProvider(15.minutes)
)

fun CacheConfig.redisProvider(
    host: String,
    port: Int,
    password: String? = null,
    database: Int = 0,
    ssl: Boolean = false,
    expireTime: Duration
) {
    provider = RedisCacheProvider(host, port, password, database, ssl, expireTime)
}

fun CacheConfig.memoryProvider(expireTime: Duration) {
    provider = MemoryCacheProvider(expireTime)
}

val Cache = createApplicationPlugin("Cache", ::CacheConfig) {
    application.attributes.put(AttributeKey("CacheProvider"), pluginConfig.provider)
}

class RouteCachePluginConfig(
    var specifyQueryParams: List<String> = emptyList(),
    var cachedNoQueryParamRequest: Boolean = true,
    var expireTime: Duration? = null
)

val CacheRoutePlugin = createRouteScopedPlugin("CacheRoutePlugin", ::RouteCachePluginConfig) {
    val provider = application.attributes.getOrNull(AttributeKey<CacheProvider>("CacheProvider"))
        ?: throw IllegalStateException("CacheProvider not found")
    val isCache = AttributeKey<Boolean>("isCache")
    val expireTime = pluginConfig.expireTime

    fun buildKey(call: ApplicationCall) = if (pluginConfig.specifyQueryParams.isEmpty()) call.request.uri
    else pluginConfig.specifyQueryParams.joinToString("&") { "$it=${call.request.queryParameters[it]}" }.let {
        if (it.isBlank()) call.request.uri else call.request.uri + "?" + it
    }

    onCall { call ->
        val queryParams = call.request.queryParameters
        if (queryParams.isEmpty() && !pluginConfig.cachedNoQueryParamRequest) return@onCall
        val key = buildKey(call)
        val cache = provider.loadCache(key) ?: return@onCall
        call.attributes.put(isCache, true)
        call.respond(cache)
        logger.info { "Cache hit: $key" }
    }
    onCallRespond { call, body ->
        if ((call.response.status()?.value
                ?: HttpStatusCode.OK.value) >= HttpStatusCode.BadRequest.value
        ) return@onCallRespond
        val queryParams = call.request.queryParameters
        if (queryParams.isEmpty() && !pluginConfig.cachedNoQueryParamRequest) return@onCallRespond
        if (call.attributes.getOrNull(isCache) == true) return@onCallRespond
        val key = buildKey(call)
        if (expireTime == null) provider.saveCache(key, body)
        else provider.saveCache(key, body, expireTime)
        logger.info { "Cache saved: $key" }
    }
    on(CallFailed) { call, e ->
        throw e
    }
}

class CacheOutputSelector: RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
        RouteSelectorEvaluation.Transparent
}

fun Route.cache(
    specifyQueryParams: List<String> = emptyList(),
    cachedNoQueryParamRequest: Boolean = true,
    expireTime: Duration? = null,
    block: Route.() -> Unit
): Route {
    val route = createChild(CacheOutputSelector())
    route.install(CacheRoutePlugin) {
        this.specifyQueryParams = specifyQueryParams
        this.cachedNoQueryParamRequest = cachedNoQueryParamRequest
        this.expireTime = expireTime
    }
    route.block()
    return route
}

abstract class CacheProvider(
    val expireTime: Duration = 15.minutes,
    val jsonMapper: Gson = GsonBuilder()
        .registerTypeAdapter(ByteArray::class.java, JsonSerializer<ByteArray> { src, _, _ ->
            JsonPrimitive(src.encodeBase64())
        })
        .registerTypeAdapter(ByteArray::class.java, JsonDeserializer { json, _, _ ->
            json.asString.decodeBase64Bytes()
        })
        .create()
) {
    private val mutex = Mutex()

    suspend fun saveCache(key: String, value: Any, expireTime: Duration = this.expireTime) {
        if (mutex.tryLock()) {
            try {
                setCache(key, value, expireTime)
            } finally {
                mutex.unlock()
            }
        }
    }

    suspend fun loadCache(key: String): Any? {
        var cache = getCache(key)
        if (cache != null) return cache

        for (i in 1..2) {
            delay(50.milliseconds)
            cache = getCache(key)
            if (cache != null) break
        }
        return cache
    }

    internal abstract suspend fun setCache(key: String, value: Any, expireTime: Duration = this.expireTime)
    internal abstract suspend fun getCache(key: String): Any?
}


@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisCacheProvider(
    host: String,
    port: Int = 6379,
    password: String? = null,
    database: Int = 0,
    ssl: Boolean = false,
    expireTime: Duration
) : CacheProvider(expireTime) {
    private val redisClient: RedisClient = RedisClient.create(
        RedisURI.Builder
            .redis(host)
            .withPort(port)
            .withSsl(ssl)
            .withDatabase(database)
            .also { if (password != null) it.withPassword(password.toCharArray()) }
            .build()
    )
    private val redisConnection = redisClient.connect()

    override suspend fun setCache(key: String, value: Any, expireTime: Duration) {
        val commands = redisConnection.coroutines()
        commands.psetex(key, expireTime.inWholeMilliseconds, value.toRedisStoreValue())
    }

    override suspend fun getCache(key: String): Any? {
        val coroutinesCommands = redisConnection.coroutines()
        return coroutinesCommands.get(key)?.toObject()
    }

    private fun Any.toRedisStoreValue() = this.javaClass.name + "^^^" + jsonMapper.toJson(this)
    private fun String.toObject(): Any {
        val split = this.split("^^^")
        val className = split[0]
        val json = split[1]
        return jsonMapper.fromJson(json, Class.forName(className))
    }
}

class MemoryCacheProvider(
    expireTime: Duration
) : CacheProvider(expireTime) {
    val cache: Cache<String, Any> = Caffeine.newBuilder()
        .expireAfterAccess(expireTime.toJavaDuration())
        .build()

    override suspend fun setCache(key: String, value: Any, expireTime: Duration) {
        cache.put(key, value)
    }

    override suspend fun getCache(key: String): Any? {
        return cache.getIfPresent(key)
    }
}























