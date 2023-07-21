package com.example.demo.models.posts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


typealias PostsFlow = Flow<Result<List<Post>>>
typealias PostFlow = Flow<Result<Post>>

class PostDataRepository(
    private val dataSource: PostDataSource
) {
    suspend fun getPosts(): PostsFlow = flow {
        val posts = dataSource.getPosts()

        if (posts != null) {
            if (posts.isEmpty()) emit(Result.success(emptyList()))
            else emit(Result.success(posts))
        } else {
            emit(Result.failure(Throwable("invalid")))
        }
    }

    suspend fun getPost(id: String?): PostFlow = flow {
        val post = dataSource.getPost(id)

        if (post != null) emit(Result.success(post))
        else emit(Result.failure(Throwable("invalid")))
    }
}