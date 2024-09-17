package io.sakurasou.model.dao.album

import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.entity.Album
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
            .map {
                Album(
                    it[Albums.id].value,
                    it[Albums.userId],
                    it[Albums.name],
                    it[Albums.description],
                    it[Albums.imageCount],
                    it[Albums.isUncategorized],
                    it[Albums.createTime],
                )
            }
    }

    override fun getAlbumById(albumId: Long): Album? {
        return Albums.selectAll()
            .where { Albums.id eq albumId }
            .map {
                Album(
                    it[Albums.id].value,
                    it[Albums.userId],
                    it[Albums.name],
                    it[Albums.description],
                    it[Albums.imageCount],
                    it[Albums.isUncategorized],
                    it[Albums.createTime],
                )
            }
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
}