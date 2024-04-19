package `in`.uidai.gov.galleryimageviewer.utility

import android.graphics.Bitmap
import android.util.LruCache
import java.lang.Runtime.*

object MemoryCache {
    private val MAX_MEMORY = getRuntime().maxMemory().toInt() / 1024
    private val CACHE_SIZE = MAX_MEMORY / 8
    private val memoryCache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun putBitmap(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
    }

    fun getBitmap(url: String): Bitmap? {
        return memoryCache.get(url)
    }
}