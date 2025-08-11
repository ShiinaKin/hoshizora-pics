package io.sakurasou.exception.service.image.io.s3

import io.sakurasou.exception.ServiceThrowable
import software.amazon.awssdk.services.s3.model.S3Exception

/**
 * @author Shiina Kin
 * 2024/12/19 12:39
 */
class S3ClientException(
    s3Exception: S3Exception,
) : ServiceThrowable() {
    override val code: Int
        get() = 5000
    override var message: String = "S3 client error, please check the configuration"

    init {
        message += ", status code: ${s3Exception.statusCode()}"
    }
}
