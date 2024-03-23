package com.example.kotlin16.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin16.MainActivity
import com.example.kotlin16.databinding.ItemPostBinding
import com.example.kotlin16.model.Post
import okhttp3.*
import okio.IOException

class PostsAdapter(private val context: Context, private var posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val client = OkHttpClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.titleTextView.text = post.title
            binding.idTextView.text = "ID: ${post.id}"
            binding.root.setOnClickListener {
                val editTextView = EditText(context).apply {
                    setText(post.title)
                }
                AlertDialog.Builder(context).apply {
                    setTitle("Редактирование поста")
                    setView(editTextView)
                    setPositiveButton("Применить") { _, _ ->
                        val updatedTitle = editTextView.text.toString()
                        val updatedPost = post.copy(title = updatedTitle)
                        updatePost(updatedPost, adapterPosition)
                    }
                    setNegativeButton("Отмена", null)
                }.show()
            }
        }
    }

    private fun updatePost(post: Post, position: Int) {
        posts = posts.toMutableList().apply {
            set(position, post)
        }
        (context as? MainActivity)?.runOnUiThread {
            notifyItemChanged(position)
        }
        val requestBody = FormBody.Builder()
            .add("title", post.title)
            .add("body", post.body)
            .add("userId", post.userId.toString())
            .build()
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts/${post.id}")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("PostAdapter:updatePost", "Failure of POST-request")
                }
            }
        })
    }
}