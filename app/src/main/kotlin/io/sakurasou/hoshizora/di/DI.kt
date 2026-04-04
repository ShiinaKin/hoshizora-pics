package io.sakurasou.hoshizora.di

import kotlin.reflect.KClass

/**
 * @author Shiina Kin
 * 2026/4/2 22:46
 */

object DIManager {
    private val diInstance = DI()

    fun getDIInstance() = diInstance
}

interface DIRegistryScope {
    fun <T : Any> register(
        clazz: KClass<T>,
        factory: (DIGetScope) -> T,
    )
}

interface DIGetScope {
    fun <T : Any> get(clazz: KClass<T>): T
}

class DI :
    DIRegistryScope,
    DIGetScope {
    private val factoryMap: MutableMap<KClass<*>, (DIGetScope) -> Any> = mutableMapOf()
    private val instanceMap: MutableMap<KClass<*>, Any> = mutableMapOf()

    private val mutex = Any()

    override fun <T : Any> register(
        clazz: KClass<T>,
        factory: (DIGetScope) -> T,
    ) {
        synchronized(mutex) {
            // factoryMap[clazz] = factory
            // if (instanceMap.contains(clazz)) instanceMap[clazz] = factory(this)
            if (factoryMap.contains(clazz)) throw RuntimeException("Already registered for ${clazz.qualifiedName}")
            factoryMap[clazz] = factory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(clazz: KClass<T>): T {
        synchronized(mutex) {
            return instanceMap.getOrPut(clazz) {
                val factory =
                    factoryMap[clazz]
                        ?: throw RuntimeException("No factory found for class: ${clazz.qualifiedName}")
                factory(this)
            } as T
        }
    }
}

inline fun diOperation(block: DI.() -> Unit) = block(DIManager.getDIInstance())

inline fun <reified T : Any> DIRegistryScope.register(noinline factory: (DIGetScope) -> T) = register(T::class, factory)

inline fun <reified T : Any> DIGetScope.get(): T = get(T::class)

inline fun <reified T : Any> inject() = lazy { DIManager.getDIInstance().get<T>() }
