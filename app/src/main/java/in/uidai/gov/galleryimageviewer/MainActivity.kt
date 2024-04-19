
package `in`.uidai.gov.galleryimageviewer

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.uidai.gov.galleryimageviewer.databinding.ActivityMainBinding
import `in`.uidai.gov.galleryimageviewer.factory.ImageAdapter
import `in`.uidai.gov.galleryimageviewer.factory.ImageAdapter2
import `in`.uidai.gov.galleryimageviewer.factory.NewsImages
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private var imageUrls:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        initializeData2()
    }

    private fun initializeData() {
        val url = "https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100"
        ImageLoaderTask().execute(url)
    }

    private inner class ImageLoaderTask : AsyncTask<String, Void, List<String>>() {

        override fun doInBackground(vararg urls: String): List<String> {
            val imageUrlList = mutableListOf<String>()
            try {
                val connection = URL(urls[0]).openConnection() as HttpURLConnection
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    imageUrlList.addAll(extractImageUrls(response))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return imageUrlList
        }

        override fun onPostExecute(result: List<String>?) {
            result?.let {
                adapter = ImageAdapter(it)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun extractImageUrls(jsonString: String): List<String> {
        val imageUrls = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val thumbnailObject = jsonObject.getJSONObject("thumbnail")
                val domain = thumbnailObject.getString("domain")
                val basePath = thumbnailObject.getString("basePath")
                val key = thumbnailObject.getString("key")
                val imageUrl = "$domain/$basePath/0/$key"
                imageUrls.add(imageUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageUrls
    }

    private fun initializeData2() {

        val imageList = getImagesList(this)
        for(item in imageList){
            val domain = item.thumbnail?.domain
            val base_path = item.thumbnail?.basePath
            val image_key = item.thumbnail?.key

            val url = "$domain/$base_path/0/$image_key"
            Log.e("url String: ",url)
            imageUrls.add(url)
            // imageUrls.add(item.coverageURL)
        }
        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = ImageAdapter2(imageUrls)
        binding.recyclerView.adapter = adapter
    }

    private fun getImagesList(context: Context): List<NewsImages> {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("images_list.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Log.d("Tag:", ioException.toString())
        }

        val listCountryType = object : TypeToken<List<NewsImages>>() {}.type
        return Gson().fromJson(jsonString, listCountryType)
    }
}
