package io.sakurasou.hoshizora.native

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.hoshizora.di.inject
import io.sakurasou.hoshizora.exception.native.ImageOperationException
import io.sakurasou.hoshizora.model.group.ImageType
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

/**
 * @author Shiina Kin
 * 2026/4/9 19:36
 */
object ImageOperation {
    private const val MAGICK_BOOLEAN_TRUE = 1
    private const val MAX_EXCEPTION_MESSAGE_SIZE = 4096L
    private val methodHandleDict = mutableMapOf<String, MethodHandle>()

    init {
        val logger = KotlinLogging.logger {}
        val linker: Linker by inject()
        val imageMagickLib: SymbolLookup by inject()
        context(logger, linker, imageMagickLib) {
            initMethodHandles()
            genesis()
        }
    }

    context(logger: KLogger, linker: Linker, imageMagickLib: SymbolLookup)
    private fun initMethodHandles() {
        methodHandleDict["MagickWandGenesis"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickWandGenesis"),
                FunctionDescriptor.ofVoid(),
            )
        methodHandleDict["MagickWandTerminus"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickWandTerminus"),
                FunctionDescriptor.ofVoid(),
            )
        methodHandleDict["NewMagickWand"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("NewMagickWand"),
                FunctionDescriptor.of(ValueLayout.ADDRESS),
            )
        methodHandleDict["DestroyMagickWand"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("DestroyMagickWand"),
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickRelinquishMemory"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickRelinquishMemory"),
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickReadImageBlob"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickReadImageBlob"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG),
            )
        methodHandleDict["MagickGetImageBlob"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickGetImageBlob"),
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickWriteImage"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickWriteImage"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickResizeImage"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickResizeImage"),
                FunctionDescriptor.of(
                    ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.JAVA_INT,
                ),
            )
        methodHandleDict["MagickScaleImage"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickScaleImage"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
            )
        methodHandleDict["MagickThumbnailImage"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickThumbnailImage"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
            )
        methodHandleDict["MagickSetImageFormat"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickSetImageFormat"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickSetImageCompressionQuality"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickSetImageCompressionQuality"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG),
            )
        methodHandleDict["MagickGetExceptionType"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickGetExceptionType"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS),
            )
        methodHandleDict["MagickGetException"] =
            linker.downcallHandle(
                imageMagickLib.findOrThrow("MagickGetException"),
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            )
        logger.info { "Initialized ImageMagick method handles" }
    }

    context(logger: KLogger)
    fun genesis() {
        val magickWandGenesisCall = methodHandleDict.getValue("MagickWandGenesis")
        logger.info { "Initializing ImageMagick runtime" }
        magickWandGenesisCall.invoke()
        logger.info { "ImageMagick initialized" }
    }

    context(logger: KLogger)
    fun terminus() {
        val magickWandTerminusCall = methodHandleDict.getValue("MagickWandTerminus")
        logger.info { "Terminating ImageMagick runtime" }
        magickWandTerminusCall.invoke()
        logger.info { "ImageMagick terminated" }
    }

    context(logger: KLogger)
    fun newMagickWand(): MemorySegment {
        val newMagickWandCall = methodHandleDict.getValue("NewMagickWand")
        val magickWandPtr = newMagickWandCall.invoke() as MemorySegment
        if (magickWandPtr == MemorySegment.NULL) {
            logger.error { "Failed to create MagickWand: native call returned NULL" }
            throw ImageOperationException("Failed to create MagickWand: native call returned NULL")
        }
        logger.debug { "Created MagickWand pointer=${magickWandPtr.pointerString()}" }
        return magickWandPtr
    }

    context(logger: KLogger)
    fun destroyMagickWand(magickWandPtr: MemorySegment) {
        val destroyMagickWandCall = methodHandleDict.getValue("DestroyMagickWand")
        destroyMagickWandCall.invoke(magickWandPtr)
        logger.debug { "Destroyed MagickWand pointer=${magickWandPtr.pointerString()}" }
    }

    context(logger: KLogger)
    fun relinquishMemory(imageBlobPtr: MemorySegment) {
        val relinquishMemoryCall = methodHandleDict.getValue("MagickRelinquishMemory")
        relinquishMemoryCall.invoke(imageBlobPtr)
        logger.debug { "Relinquished native memory pointer=${imageBlobPtr.pointerString()}" }
    }

    context(logger: KLogger, arena: Arena)
    fun readImageBlob(
        magickWandPtr: MemorySegment,
        bytes: ByteArray,
    ) {
        /**
         * MagickBooleanType MagickReadImageBlob(MagickWand *wand, const void *blob,const size_t length)
         */
        val readImageBlobCall = methodHandleDict.getValue("MagickReadImageBlob")

        val imageBytePtr = arena.allocate(bytes.size.toLong())
        MemorySegment.copy(bytes, 0, imageBytePtr, ValueLayout.JAVA_BYTE, 0, bytes.size)

        logger.debug {
            "Reading image blob into wand pointer=${magickWandPtr.pointerString()}, byteSize=${bytes.size}"
        }
        val result = readImageBlobCall.invoke(magickWandPtr, imageBytePtr, bytes.size.toLong()) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickReadImageBlob", magickWandPtr)
        logger.debug {
            "Read image blob into wand pointer=${magickWandPtr.pointerString()}, byteSize=${bytes.size}"
        }
    }

    context(logger: KLogger, arena: Arena)
    fun getImageBlob(magickWandPtr: MemorySegment): ImageBlob {
        /**
         * unsigned char *MagickGetImageBlob(MagickWand *wand,size_t *length)
         */
        val magickGetImageBlobCall = methodHandleDict.getValue("MagickGetImageBlob")

        val imageSizePtr = arena.allocate(ValueLayout.JAVA_LONG)
        val imageBlobPtr = magickGetImageBlobCall.invoke(magickWandPtr, imageSizePtr) as MemorySegment
        if (imageBlobPtr == MemorySegment.NULL) {
            failOperation("MagickGetImageBlob", magickWandPtr)
        }
        val imageSize = imageSizePtr.get(ValueLayout.JAVA_LONG, 0)
        val imageBytes = imageBlobPtr.reinterpret(imageSize).asReadOnly().toArray(ValueLayout.JAVA_BYTE)
        logger.debug {
            "Fetched image blob from wand pointer=${magickWandPtr.pointerString()}, byteSize=$imageSize"
        }
        return ImageBlob(
            size = imageSize,
            blobPtr = imageBlobPtr,
            bytes = imageBytes,
        )
    }

    context(logger: KLogger, arena: Arena)
    fun writeImage(
        magickWandPtr: MemorySegment,
        outputAbsPath: String,
    ) {
        /**
         * MagickBooleanType MagickWriteImage(MagickWand *wand, const char *filename)
         */
        val magickWriteImageCall = methodHandleDict.getValue("MagickWriteImage")
        logger.debug {
            "Writing image from wand pointer=${magickWandPtr.pointerString()} to path=$outputAbsPath"
        }
        val result = magickWriteImageCall.invoke(magickWandPtr, arena.allocateFrom(outputAbsPath)) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickWriteImage", magickWandPtr)
        logger.debug {
            "Wrote image from wand pointer=${magickWandPtr.pointerString()} to path=$outputAbsPath"
        }
    }

    context(logger: KLogger)
    fun resizeImage(
        magickWandPtr: MemorySegment,
        width: Long,
        height: Long,
        filter: FilterType = FilterType.LanczosFilter,
    ) {
        /**
         * MagickBooleanType MagickResizeImage(MagickWand *wand, const size_t columns,const size_t rows,const FilterType filter)
         */
        val magickResizeImageCall = methodHandleDict.getValue("MagickResizeImage")
        logger.debug {
            "Resizing image in wand pointer=${magickWandPtr.pointerString()} to ${width}x$height"
        }
        val result = magickResizeImageCall.invoke(magickWandPtr, width, height, filter.ordinal) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickResizeImage", magickWandPtr)
        logger.debug {
            "Resized image in wand pointer=${magickWandPtr.pointerString()} to ${width}x$height"
        }
    }

    context(logger: KLogger)
    fun scaleImage(
        magickWandPtr: MemorySegment,
        width: Long,
        height: Long,
    ) {
        /**
         * MagickBooleanType MagickScaleImage(MagickWand *wand, const size_t columns,const size_t rows)
         */
        val magickScaleImageCall = methodHandleDict.getValue("MagickScaleImage")
        logger.debug {
            "Scaling image in wand pointer=${magickWandPtr.pointerString()} to ${width}x$height"
        }
        val result = magickScaleImageCall.invoke(magickWandPtr, width, height) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickScaleImage", magickWandPtr)
        logger.debug {
            "Scaled image in wand pointer=${magickWandPtr.pointerString()} to ${width}x$height"
        }
    }

    context(logger: KLogger)
    fun thumbnailImage(
        magickWandPtr: MemorySegment,
        width: Long,
        height: Long,
    ) {
        /**
         * MagickBooleanType MagickThumbnailImage(MagickWand *wand, const size_t columns,const size_t rows)
         */
        val magickThumbnailImageCall = methodHandleDict.getValue("MagickThumbnailImage")
        logger.debug {
            "Generating thumbnail in wand pointer=${magickWandPtr.pointerString()} with bounds ${width}x$height"
        }
        val result = magickThumbnailImageCall.invoke(magickWandPtr, width, height) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickThumbnailImage", magickWandPtr)
        logger.debug {
            "Generated thumbnail in wand pointer=${magickWandPtr.pointerString()} with bounds ${width}x$height"
        }
    }

    context(logger: KLogger, arena: Arena)
    fun setImageFormat(
        magickWandPtr: MemorySegment,
        type: ImageType,
    ) {
        /**
         * MagickBooleanType MagickSetImageFormat(MagickWand *wand, const char *format)
         */
        val magickSetImageFormatCall = methodHandleDict.getValue("MagickSetImageFormat")
        logger.debug {
            "Setting image format for wand pointer=${magickWandPtr.pointerString()} to ${type.name}"
        }
        val result = magickSetImageFormatCall.invoke(magickWandPtr, arena.allocateFrom(type.name)) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickSetImageFormat", magickWandPtr)
        logger.debug {
            "Set image format for wand pointer=${magickWandPtr.pointerString()} to ${type.name}"
        }
    }

    context(logger: KLogger)
    fun setImageQuality(
        magickWandPtr: MemorySegment,
        quality: Double,
    ) {
        /**
         * MagickBooleanType MagickSetImageCompressionQuality(MagickWand *wand, const size_t quality)
         */
        val magickSetImageCompressionQualityCall = methodHandleDict.getValue("MagickSetImageCompressionQuality")
        val normalizedQuality = (quality * 100).toLong().coerceIn(0, 100)
        logger.debug {
            "Setting image quality for wand pointer=${magickWandPtr.pointerString()} to $normalizedQuality"
        }
        val result = magickSetImageCompressionQualityCall.invoke(magickWandPtr, normalizedQuality) == MAGICK_BOOLEAN_TRUE
        requireSuccess(result, "MagickSetImageCompressionQuality", magickWandPtr)
        logger.debug {
            "Set image quality for wand pointer=${magickWandPtr.pointerString()} to $normalizedQuality"
        }
    }

    context(logger: KLogger)
    fun getExceptionType(magickWandPtr: MemorySegment): Long {
        /**
         * ExceptionType MagickGetExceptionType(const MagickWand *)
         */
        val magickGetExceptionTypeCall = methodHandleDict.getValue("MagickGetExceptionType")
        val exceptionType = magickGetExceptionTypeCall.invoke(magickWandPtr) as Int
        logger.debug {
            "Fetched ImageMagick exception type=$exceptionType from wand pointer=${magickWandPtr.pointerString()}"
        }
        return exceptionType.toLong()
    }

    context(logger: KLogger)
    fun getException(magickWandPtr: MemorySegment): String {
        /**
         * char *MagickGetException(const MagickWand *,ExceptionType *)
         */
        val exceptionInfo = fetchExceptionInfo(magickWandPtr)
        logger.debug {
            "Fetched ImageMagick exception from wand pointer=${magickWandPtr.pointerString()} " +
                "[type=${exceptionInfo.type}]: ${exceptionInfo.message}"
        }
        return exceptionInfo.message
    }

    context(logger: KLogger)
    private fun requireSuccess(
        succeeded: Boolean,
        operation: String,
        magickWandPtr: MemorySegment,
    ) {
        if (!succeeded) failOperation(operation, magickWandPtr)
    }

    context(logger: KLogger)
    private fun failOperation(
        operation: String,
        magickWandPtr: MemorySegment,
    ): Nothing {
        val exceptionInfo = fetchExceptionInfo(magickWandPtr)
        val message = "ImageMagick $operation failed [type=${exceptionInfo.type}]: ${exceptionInfo.message}"
        logger.error { message }
        throw ImageOperationException(message)
    }

    context(logger: KLogger)
    private fun fetchExceptionInfo(magickWandPtr: MemorySegment): MagickExceptionInfo =
        Arena.ofConfined().use { arena ->
            val magickGetExceptionCall = methodHandleDict.getValue("MagickGetException")
            val exceptionTypePtr = arena.allocate(ValueLayout.JAVA_INT)
            val exceptionMsgPtr = magickGetExceptionCall.invoke(magickWandPtr, exceptionTypePtr) as MemorySegment
            val exceptionType = exceptionTypePtr.get(ValueLayout.JAVA_INT, 0)
            if (exceptionMsgPtr == MemorySegment.NULL) {
                return@use MagickExceptionInfo(
                    type = exceptionType,
                    message = "Unknown ImageMagick error",
                )
            }

            val exceptionMessage =
                try {
                    exceptionMsgPtr.reinterpret(MAX_EXCEPTION_MESSAGE_SIZE).getString(0)
                } finally {
                    relinquishMemory(exceptionMsgPtr)
                }

            MagickExceptionInfo(
                type = exceptionType,
                message = exceptionMessage.ifBlank { "Unknown ImageMagick error" },
            )
        }

    private fun MemorySegment.pointerString(): String = "0x${address().toString(16)}"
}

/**
 * https://github.com/ImageMagick/ImageMagick/blob/51291c38859f78e9c03fef061e8c310354a4d07a/MagickCore/resample.h#L69
 */
@Suppress("unused")
enum class FilterType {
    UndefinedFilter,
    PointFilter,
    BoxFilter,
    TriangleFilter,
    HermiteFilter,
    HannFilter,
    HammingFilter,
    BlackmanFilter,
    GaussianFilter,
    QuadraticFilter,
    CubicFilter,
    CatromFilter,
    MitchellFilter,
    JincFilter,
    SincFilter,
    SincFastFilter,
    KaiserFilter,
    WelchFilter,
    ParzenFilter,
    BohmanFilter,
    BartlettFilter,
    LagrangeFilter,
    LanczosFilter,
    LanczosSharpFilter,
    Lanczos2Filter,
    Lanczos2SharpFilter,
    RobidouxFilter,
    RobidouxSharpFilter,
    CosineFilter,
    SplineFilter,
    LanczosRadiusFilter,
    CubicSplineFilter,
    MagicKernelSharp2013Filter,
    MagicKernelSharp2021Filter,

    // a count of all the filters, not a real filter
    SentinelFilter,
}

private data class MagickExceptionInfo(
    val type: Int,
    val message: String,
)

data class ImageBlob(
    val size: Long,
    val blobPtr: MemorySegment,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageBlob

        if (size != other.size) return false
        if (blobPtr != other.blobPtr) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + blobPtr.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
