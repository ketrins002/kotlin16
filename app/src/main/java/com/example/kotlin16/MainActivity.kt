package com.example.kotlin16

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlin16.adapter.PostsAdapter
import com.example.kotlin16.databinding.ActivityMainBinding
import com.example.kotlin16.model.Post
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadPosts()
    }

    private fun loadPosts() {
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity:loadPosts", "Failed to load posts", e)
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("MainActivity", "Failed to load posts: ${response.message}")
                    return
                }
                response.body?.string()?.let {
                    val postsList = parsePosts(it)
                    runOnUiThread {
                        binding.postsRecyclerView.adapter = PostsAdapter(this@MainActivity, postsList)
                    }
                }
            }
        })
    }

    private fun parsePosts(jsonData: String): List<Post> {
        val jsonArray = JSONArray(jsonData)
        val posts = mutableListOf<Post>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val post = Post(
                jsonObject.getInt("userId"),
                jsonObject.getInt("id"),
                jsonObject.getString("title"),
                jsonObject.getString("body")
            )
            posts.add(post)
        }
        return posts
    }
}
