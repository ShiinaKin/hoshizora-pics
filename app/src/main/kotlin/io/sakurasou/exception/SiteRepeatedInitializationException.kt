package io.sakurasou.exception

/**
 * @author Shiina Kin
 * 2024/9/13 16:13
 */
class SiteRepeatedInitializationException: ServiceThrowable() {
    override val code: Int
        get() = 5000
    override val message: String
        get() = "Site has been initialized"
}