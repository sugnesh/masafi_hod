package com.engear.vansales.utils

import android.content.Context;
import android.graphics.Bitmap;
import io.reactivex.Flowable
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

class FileCompressor(context:Context) {
    //max width and height values of the compressed image is taken as 612x816
    private var maxWidth = 612
    private var maxHeight = 816
    private var compressFormat = Bitmap.CompressFormat.JPEG
    private var quality = 80
    private var destinationDirectoryPath:String
    init{
        destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images"
    }
    fun setMaxWidth(maxWidth:Int):FileCompressor {
        this.maxWidth = maxWidth
        return this
    }
    fun setMaxHeight(maxHeight:Int):FileCompressor {
        this.maxHeight = maxHeight
        return this
    }
    fun setCompressFormat(compressFormat:Bitmap.CompressFormat):FileCompressor {
        this.compressFormat = compressFormat
        return this
    }
    fun setQuality(quality:Int):FileCompressor {
        this.quality = quality
        return this
    }
    fun setDestinationDirectoryPath(destinationDirectoryPath:String):FileCompressor {
        this.destinationDirectoryPath = destinationDirectoryPath
        return this
    }
    @Throws(IOException::class)
    @JvmOverloads fun compressToFile(imageFile:File, compressedFileName:String = imageFile.getName()):File {
        return ImageUtil.compressImage(imageFile, maxWidth, maxHeight, compressFormat, quality,
            destinationDirectoryPath + File.separator + compressedFileName)
    }
    @Throws(IOException::class)
    fun compressToBitmap(imageFile:File):Bitmap {
        return ImageUtil.decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight)
    }
    @JvmOverloads fun compressToFileAsFlowable(imageFile:File,
                                               compressedFileName:String = imageFile.getName()): Flowable<File> {
        return Flowable.defer(object: Callable<Flowable<File>> {
            override fun call():Flowable<File> {
                try
                {
                    return Flowable.just(compressToFile(imageFile, compressedFileName))
                }
                catch (e:IOException) {
                    return Flowable.error(e)
                }
            }
        })
    }
    fun compressToBitmapAsFlowable(imageFile:File):Flowable<Bitmap> {
        return Flowable.defer(object: Callable<Flowable<Bitmap>> {
            override fun call():Flowable<Bitmap> {
                try
                {
                    return Flowable.just(compressToBitmap(imageFile))
                }
                catch (e:IOException) {
                    return Flowable.error(e)
                }
            }
        })
    }
}