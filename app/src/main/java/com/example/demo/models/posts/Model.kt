package com.example.demo.models.posts

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

sealed class PostUiState {
    object Loading : PostUiState()
    data class LoadedPosts(val posts: List<Post>?) : PostUiState()
    data class LoadedPost(val post: Post?) : PostUiState()
    data class Error(val message: String) : PostUiState()
}

sealed class PostUiEvent {
    object Fetch : PostUiEvent()
    object FetchSingle : PostUiEvent()
}

