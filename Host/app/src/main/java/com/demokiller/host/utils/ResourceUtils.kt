package com.demokiller.host.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.util.DisplayMetrics
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

object ResourceUtils {
    @JvmStatic
    val resources: Resources
        get() {
            val activity = AppUtils.getCurrentActivity()
            return if (activity != null) activity.resources else AppUtils.getContext().resources
        }

    val assets: AssetManager
        get() = AppUtils.getContext().assets

    fun getString(resId: Int): String {
        return AppUtils.getContext().getString(resId)
    }

    @JvmStatic
    val displayMetrics: DisplayMetrics
        get() = resources.displayMetrics
    val contentResolver: ContentResolver
        get() = AppUtils.getContext().contentResolver

    fun getColor(id: Int): Int {
        return resources.getColor(id)
    }

    fun getDimension(id: Int): Float {
        return resources.getDimension(id)
    }

    fun getDimensionPixelSize(id: Int): Float {
        return resources.getDimensionPixelSize(id).toFloat()
    }

    @JvmStatic
    fun getDrawable(id: Int): Drawable {
        return resources.getDrawable(id)
    }

    fun getString(id: Int, vararg formatArgs: Any?): String {
        return resources.getString(id, *formatArgs)
    }

    fun getImageContentUri(context: Context, path: String?): Uri {
        var path = path
        if (path == null) {
            path = ""
        }
        return if (path.startsWith("content://")) {
            Uri.parse(path)
        } else {
            val file = File(path)
            FileProvider.getUriForFile(
                context,
                context.packageName + ".fileProvider",
                file
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun uriToFileApiQ(uri: Uri?, context: Context?): File? {
        var file: File? = null
        if (uri == null || uri.scheme == null || context == null) return null
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = uri.path?.let { File(it) }
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val displayName = (System.currentTimeMillis() + ((Math.random() + 1) * 1000).roundToInt()
                    ).toString() + "." + MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri))
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val cache = File(context.cacheDir.absolutePath, displayName)
                val fos = FileOutputStream(cache)
                FileUtils.copy(inputStream!!, fos)
                file = cache
                fos.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }
}

fun Int.string(): String {
    return ResourceUtils.getString(this)
}