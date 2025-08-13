package io.sakurasou.hoshizora.service.common

import io.sakurasou.hoshizora.controller.request.SiteInitRequest
import io.sakurasou.hoshizora.controller.vo.CommonSiteSetting
import io.sakurasou.hoshizora.model.dto.ImageFileDTO

/**
 * @author Shiina Kin
 * 2024/9/13 16:23
 */
interface CommonService {
    suspend fun initSite(siteInitRequest: SiteInitRequest)

    suspend fun fetchCommonSiteSetting(): CommonSiteSetting

    suspend fun fetchImage(imageUniqueName: String): ImageFileDTO

    suspend fun fetchRandomImage(): ImageFileDTO
}
