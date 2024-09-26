package io.sakurasou.model.dao.album

import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.entity.Album
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
class AlbumDaoImpl : AlbumDao {
    override fun listAlbumByUserId(userId: Long): List<Album> {
        return Albums.selectAll()
            .where { Albums.userId eq userId }
            .map { toAlbum(it) }
    }

    override fun findAlbumById(albumId: Long): Album? {
        return Albums.selectAll()
            .where { Albums.id eq albumId }
            .map { toAlbum(it) }
            .firstOrNull()
    }

    override fun saveAlbum(insertDTO: AlbumInsertDTO): Long {
        val entityID = Albums.insertAndGetId {
            it[userId] = insertDTO.userId
            it[name] = insertDTO.name
            it[description] = insertDTO.description
            it[imageCount] = insertDTO.imageCount
            it[isUncategorized] = insertDTO.isUncategorized
            it[createTime] = insertDTO.createTime
        }
        return entityID.value
    }

    override fun initAlbumForUser(userId: Long): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val uncategorizedAlbum = AlbumInsertDTO(
            userId = userId,
            name = "uncategorized",
            description = "default, cannot delete",
            imageCount = 0,
            isUncategorized = true,
            createTime = now
        )
        return saveAlbum(uncategorizedAlbum)
    }

    private fun toAlbum(it: ResultRow) = Album(
        it[Albums.id].value,
        it[Albums.userId],
        it[Albums.name],
        it[Albums.description],
        it[Albums.imageCount],
        it[Albums.isUncategorized],
        it[Albums.createTime],
    )
}