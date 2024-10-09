package io.sakurasou.model.dao.image

import io.sakurasou.model.dto.ImageCountAndTotalSizeDTO
import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.entity.Image
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */
class ImageDaoImpl : ImageDao {
    override fun saveImage(insertDTO: ImageInsertDTO): Long {
        val entityID = Images.insertAndGetId {
            it[userId] = insertDTO.userId
            it[groupId] = insertDTO.groupId
            it[albumId] = insertDTO.albumId
            it[name] = insertDTO.name
            it[description] = insertDTO.description
            it[path] = insertDTO.path
            it[strategyId] = insertDTO.strategyId
            it[originName] = insertDTO.originName
            it[mimeType] = insertDTO.mimeType
            it[extension] = insertDTO.extension
            it[size] = insertDTO.size
            it[width] = insertDTO.width
            it[height] = insertDTO.height
            it[md5] = insertDTO.md5
            it[sha1] = insertDTO.sha1
            it[isPrivate] = insertDTO.isPrivate
            it[createTime] = insertDTO.createTime
        }
        return entityID.value
    }

    override fun deleteImageById(imageId: Long): Int {
        return Images.deleteWhere { Images.id eq imageId }
    }

    override fun deleteImageByUserId(userId: Long): Int {
        return Images.deleteWhere { Images.userId eq userId }
    }

    override fun updateImageGroupIdByUserId(userId: Long, groupId: Long): Int {
        return Images.update({ Images.userId eq userId }) {
            it[Images.groupId] = groupId
        }
    }

    override fun updateAlbumIdByAlbumId(oldAlbumId: Long, newAlbumId: Long): Int {
        return Images.update({ Images.albumId eq oldAlbumId }) {
            it[albumId] = newAlbumId
        }
    }

    override fun listImageByAlbumId(albumId: Long): List<Image> {
        return Images.selectAll()
            .where { Images.albumId eq albumId }
            .map { toImage(it) }
    }

    override fun getImageCountAndTotalSizeOfUser(userId: Long): ImageCountAndTotalSizeDTO {
        return Images
            .select(Images.id.count(), Images.size.sum())
            .where { Images.userId eq userId }
            .first()
            .let {
                ImageCountAndTotalSizeDTO(
                    count = it[Images.id.count()],
                    totalSize = it[Images.size.sum()] ?: 0.0
                )
            }
    }

    override fun getImageById(imageId: Long): Image? {
        return Images.selectAll()
            .where { Images.id eq imageId }
            .map { toImage(it) }
            .firstOrNull()
    }

    private fun toImage(it: ResultRow) = Image(
        it[Images.id].value,
        it[Images.userId],
        it[Images.groupId],
        it[Images.albumId],
        it[Images.name],
        it[Images.description],
        it[Images.path],
        it[Images.strategyId],
        it[Images.originName],
        it[Images.mimeType],
        it[Images.extension],
        it[Images.size],
        it[Images.width],
        it[Images.height],
        it[Images.md5],
        it[Images.sha1],
        it[Images.isPrivate],
        it[Images.createTime]
    )
}