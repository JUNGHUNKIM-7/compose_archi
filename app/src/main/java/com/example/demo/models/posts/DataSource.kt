package com.example.demo.models.posts

import com.example.demo.utils.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostDataSource {
    private val baseUrl = "https://jsonplaceholder.typicode.com/posts"

    suspend fun getPosts(): List<Post>? {
        return withContext(Dispatchers.IO) {
            val res = KtorClient.client.get(baseUrl)
            return@withContext if (res.status == HttpStatusCode.OK) {
                res.body<List<Post>>()
            } else {
                null
            }
        }
    }

    suspend fun getPost(id: String?): Post? {
        if (id == null) return null

        return withContext(Dispatchers.IO) {
            val res = KtorClient.client.get("$baseUrl/$id")
            return@withContext if (res.status == HttpStatusCode.OK) {
                res.body<Post>()
            } else {
                null
            }
        }
    }
}