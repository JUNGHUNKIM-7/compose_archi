package com.example.demo.models.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PostViewModel(
    private val dataRepository: PostDataRepository
) : ViewModel() {
    private val _postsUiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val postsUiState = _postsUiState

    private val _postUiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val postUiState = _postUiState

    private fun fetch() {
        viewModelScope.launch {
            dataRepository.getPosts().collect { result ->
                when (result.isSuccess) {
                    true -> postsUiState.update {
                        PostUiState.LoadedPosts(result.getOrNull())
                    }

                    else ->
                        postsUiState.update {
                            PostUiState.Error(result.exceptionOrNull()?.message.toString())
                        }
                }
            }
        }
    }

    private fun fetchSingle(id: String?) {
        viewModelScope.launch {
            dataRepository.getPost(id).collect { result ->
                when (result.isSuccess) {
                    true -> postUiState.update {
                        PostUiState.LoadedPost(result.getOrNull())
                    }

                    else ->
                        postUiState.update {
                            PostUiState.Error(result.exceptionOrNull()?.message.toString())
                        }
                }
            }
        }
    }

    operator fun invoke(ev: PostUiEvent, id: String? = null) {
        when (ev) {
            is PostUiEvent.Fetch -> fetch()
            is PostUiEvent.FetchSingle -> fetchSingle(id)
        }
    }
}