package io.sakurasou.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:20
 */
@Serializable
data class PageResult<T>(
    val total: Int,
    val list: List<T>
)

@Serializable
data class CommonResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val isSuccessful: Boolean = code == 0,
) {
    constructor(code: Int, message: String) : this(code, message, null)

    companion object {
        fun <T> success(data: T): CommonResponse<T> {
            return CommonResponse(0, "success", data)
        }

        fun <T> error(code: Int, message: String): CommonResponse<T> {
            return CommonResponse(code, message)
        }
    }
}