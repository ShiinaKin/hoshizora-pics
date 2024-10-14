package io.sakurasou.service.common

import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.model.dto.ImageFileDTO

/**
 * @author Shiina Kin
 * 2024/9/13 16:23
 */
interface CommonService {
    suspend fun initSite(siteInitRequest: SiteInitRequest)
    suspend fun fetchImage(imageUniqueName: String): ImageFileDTO
    suspend fun fetchRandomImage(): ImageFileDTO
}