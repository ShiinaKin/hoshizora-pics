package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:36
 */
data class ImageRawFile(
    val name: String,
    val mimeType: String,
    val size: Long,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageRawFile

        if (name != other.name) return false
        if (mimeType != other.mimeType) return false
        if (size != other.size) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

@Serializable
data class ImagePatchRequest(
    val albumId: Long? = null,
    val displayName: String? = null,
    val description: String? = null,
    val isPrivate: Boolean? = null
)

@Serializable
data class ImageManagePatchRequest(
    val userId: Long? = null,
    val albumId: Long? = null,
    val displayName: String? = null,
    val description: String? = null,
    val isPrivate: Boolean? = null
)