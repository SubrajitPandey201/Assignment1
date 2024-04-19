package `in`.uidai.gov.galleryimageviewer.factory

import android.database.Observable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.uidai.gov.galleryimageviewer.R
import `in`.uidai.gov.galleryimageviewer.databinding.ItemImageBinding
import `in`.uidai.gov.galleryimageviewer.utility.Utilities
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ImageAdapter2(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImageAdapter2.ImageViewHolder>() {

    private val disposables = CompositeDisposable()

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            disposables.add(
                Utilities.loadImageFromUrl(url)!!.subscribe(
                { bitmap->binding.imageView.setImageBitmap(bitmap)},
                {error->error.printStackTrace()}
            ))
           /* CoroutineScope(Dispatchers.Main).launch {
                val bitmap = loadBitmap(url)
                if (bitmap != null) {
                    binding.imageView.setImageBitmap(bitmap)
                } else {
                    binding.imageView.setImageResource(R.drawable.ic_launcher_foreground)
                }
            }*/
            //binding.imageView.setImageBitmap(loadBitmap(url))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    // Replace this method with your actual image loading logic


    private suspend fun loadBitmap(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        return@withContext BitmapFactory.decodeStream(connection.inputStream)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }


}