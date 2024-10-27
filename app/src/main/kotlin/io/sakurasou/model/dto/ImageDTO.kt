package io.sakurasou.model.dto

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/5 14:44
 */
data class ImageInsertDTO(
    val userId: Long,
    val groupId: Long,
    val albumId: Long,
    val uniqueName: String,
    val displayName: String,
    val description: String? = null,
    val path: String,
    val strategyId: Long,
    val originName: String,
    val mimeType: String,
    val extension: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha256: String,
    val isPrivate: Boolean,
    val createTime: LocalDateTime
)

data class ImageCountAndTotalSizeDTO(
    val count: Long,
    val totalSize: Long
)

data class ImageUpdateDTO(
    val id: Long,
    val albumId: Long,
    val displayName: String,
    val description: String? = null,
    val isPrivate: Boolean
)

@Serializable
@Name("ImageFileVO")
data class ImageFileDTO(
    val bytes: ByteArray? = null,
    val url: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageFileDTO

        if (bytes != null) {
            if (other.bytes == null) return false
            if (!bytes.contentEquals(other.bytes)) return false
        } else if (other.bytes != null) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes?.contentHashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
    }
}