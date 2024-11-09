package io.sakurasou.model.dao.image

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.ImageManagePageVO
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dao.user.Users
import io.sakurasou.model.dto.ImageCountAndTotalSizeDTO
import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.dto.ImageUpdateDTO
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
            it[uniqueName] = insertDTO.uniqueName
            it[displayName] = insertDTO.displayName
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
            it[sha256] = insertDTO.sha256
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

    override fun updateImageById(imageUpdateDTO: ImageUpdateDTO): Int {
        return Images.update({ Images.id eq imageUpdateDTO.id }) {
            it[albumId] = imageUpdateDTO.albumId
            it[displayName] = imageUpdateDTO.displayName
            it[description] = imageUpdateDTO.description
            it[isPrivate] = imageUpdateDTO.isPrivate
        }
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

    override fun getImageCountAndTotalSizeOfUser(userId: Long): ImageCountAndTotalSizeDTO {
        return Images
            .select(Images.id.count(), Images.size.sum())
            .where { Images.userId eq userId }
            .first()
            .let {
                ImageCountAndTotalSizeDTO(
                    count = it[Images.id.count()],
                    totalSize = it[Images.size.sum()] ?: 0
                )
            }
    }

    override fun findImageById(imageId: Long): Image? {
        return Images.selectAll()
            .where { Images.id eq imageId }
            .map { toImage(it) }
            .firstOrNull()
    }

    override fun findImageByUniqueName(imageUniqueName: String): Image? {
        return Images.selectAll()
            .where { Images.uniqueName eq imageUniqueName }
            .map { toImage(it) }
            .firstOrNull()
    }

    override fun findRandomImage(): Image? {
        return Images.selectAll()
            .where { Images.isPrivate eq false }
            .orderBy(Random())
            .limit(1)
            .map { toImage(it) }
            .firstOrNull()
    }

    override fun countImageByAlbumId(albumId: Long): Long {
        return Images.select(Images.id)
            .where { Images.albumId eq albumId }
            .count()
    }

    override fun listImageByAlbumId(albumId: Long): List<Image> {
        return Images.selectAll()
            .where { Images.albumId eq albumId }
            .map { toImage(it) }
    }

    override fun pagination(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO> {
        val query = { query: Query ->
            query.adjustWhere { Images.userId eq userId }
                .also {
                    pageRequest.additionalCondition?.let { map ->
                        map["albumId"]?.let { albumId ->
                            it.andWhere { Images.albumId eq albumId.toLong() }
                        }
                        map["isPrivate"]?.let { isPrivate ->
                            it.andWhere { Images.isPrivate eq isPrivate.toBoolean() }
                        }
                        map["search"]?.let { searchContent ->
                            it.andWhere { Images.displayName like "%$searchContent%" }
                        }
                    }
                }
        }
        return fetchPage(Images, pageRequest, query) {
            ImagePageVO(
                it[Images.id].value,
                it[Images.displayName],
                it[Images.isPrivate],
                it[Images.createTime]
            )
        }
    }

    override fun paginationForManage(pageRequest: PageRequest): PageResult<ImageManagePageVO> {
        val query = { query: Query ->
            query.adjustColumnSet { join(Users, JoinType.INNER) { Users.id eq Images.userId } }
                .adjustSelect { select(Images.fields + Users.name) }
                .also {
                    pageRequest.additionalCondition?.let { map ->
                        map["userId"]?.let { userId ->
                            it.andWhere { Images.userId eq userId.toLong() }
                        }
                        map["albumId"]?.let { albumId ->
                            it.andWhere { Images.albumId eq albumId.toLong() }
                        }
                        map["isPrivate"]?.let { isPrivate ->
                            it.andWhere { Images.isPrivate eq isPrivate.toBoolean() }
                        }
                        map["search"]?.let { searchContent ->
                            it.andWhere { Images.displayName like "%$searchContent%" }
                        }
                    }
                }
        }
        return fetchPage(Images, pageRequest, query) {
            ImageManagePageVO(
                it[Images.id].value,
                it[Images.displayName],
                it[Images.userId],
                it[Users.name],
                it[Images.isPrivate],
                it[Images.createTime]
            )
        }
    }

    private fun toImage(it: ResultRow) = Image(
        it[Images.id].value,
        it[Images.userId],
        it[Images.groupId],
        it[Images.albumId],
        it[Images.uniqueName],
        it[Images.displayName],
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
        it[Images.sha256],
        it[Images.isPrivate],
        it[Images.createTime]
    )
}