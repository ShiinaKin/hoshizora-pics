package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:20
 */
@Serializable
@Name("PageResult")
data class PageResult<T>(
    val page: Long,
    val pageSize: Int,
    val total: Long,
    val totalPage: Long,
    val data: List<T>
)

@Serializable
@Name("CommonResponse")
data class CommonResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val isSuccessful: Boolean = code == 0,
) {
    constructor(code: Int, message: String) : this(code, message, null)

    companion object {
        inline fun <reified T> success(data: T): CommonResponse<T> {
            return CommonResponse(0, "success", data)
        }

        fun <T> error(code: Int, message: String): CommonResponse<T> {
            return CommonResponse(code, message)
        }
    }
}

@Serializable
@Name("CommonSiteSetting")
data class CommonSiteSetting(
    val isSiteInit: Boolean,
    val siteTitle: String,
    val siteSubTitle: String,
    val siteAllowSignup: Boolean,
)