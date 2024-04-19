package `in`.uidai.gov.galleryimageviewer.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object Utilities {

    fun loadImageFromUrl(url: String): Observable<Bitmap>? {
        return io.reactivex.Observable.create{
                emitter ->
            try {
                val imageUrl = URL(url)
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                emitter.onNext(bitmap)
                emitter.onComplete()
            }catch (e: IOException){
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}