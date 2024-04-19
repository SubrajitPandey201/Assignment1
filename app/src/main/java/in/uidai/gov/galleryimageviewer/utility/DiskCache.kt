package `in`.uidai.gov.galleryimageviewer.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleCoroutineScope
import `in`.uidai.gov.galleryimageviewer.utility.cachehandler.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream

object DiskCache {
    private const val DISK_CACHE_SIZE = 1024 * 1024 * 10 // 10MB
    private const val DISK_CACHE_SUBDIR = "images"
    private var diskLruCache: DiskLruCache? = null
    private lateinit var lifecycleCoroutineScope: LifecycleCoroutineScope

    fun init(context: Context) {
        val cacheDir = File(context.cacheDir, DISK_CACHE_SUBDIR)
        try {
            diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE.toLong())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun putBitmap(url: String, bitmap: Bitmap) {
        diskLruCache?.let { cache ->
            val key = url.hashCode().toString()
            lifecycleCoroutineScope
                .launch(Dispatchers.IO) {
                val editor = cache.edit(key)
                    withContext(Dispatchers.Main) {
                        editor?.let { edit ->
                            val outputStream: OutputStream = edit.newOutputStream(0)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            edit.commit()
                        }
                    }
            }
            /*AsyncTask.execute {
                val editor = cache.edit(key)
                try {
                    editor?.let { edit ->
                        val outputStream: OutputStream = edit.newOutputStream(0)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        edit.commit()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }*/
        }
    }

    @WorkerThread
    fun getBitmap(url: String): Bitmap? {
        diskLruCache?.let { cache ->
            val key = url.hashCode().toString()
            val snapshot = cache.get(key)
            try {
                snapshot?.let { sn ->
                    val inputStream = sn.getInputStream(0)
                    return Bitmap.createBitmap(BitmapFactory.decodeStream(inputStream))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                snapshot?.close()
            }
        }
        return null
    }
}