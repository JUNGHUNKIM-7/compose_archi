package com.example.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demo.models.*
import com.example.demo.models.posts.*
import com.example.demo.ui.theme.DemoTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val postMod = module {
    singleOf(::PostDataSource)
    singleOf(::PostDataRepository)
    viewModelOf(::PostViewModel)
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            printLogger() //Todo
            modules(postMod)
        }

        setContent {
            val navController = rememberNavController()

            DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        Box {
                            NavHost(
                                navController = navController,
                                startDestination = "posts"
                            ) {
                                composable("posts") { PostsScreen(navController) }
                                composable("posts/{id}") { bs ->
                                    val param = bs.arguments?.getString("id") ?: return@composable
                                    // + validate param
                                    PostScreen(navController, param)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostsScreen(
    navController: NavController,
    postViewModel: PostViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        println(postViewModel(PostUiEvent.Fetch))
    }

    when (val state = postViewModel.postsUiState.collectAsStateWithLifecycle().value) {
        is PostUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is PostUiState.LoadedPosts ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.posts?.let {
                    items(it.size) {
                        Button(onClick = {
                            navController.navigate("posts/${it}")
                        }) {
                            Text("go")
                        }
                    }
                }
            }

        is PostUiState.Error -> Text(state.message)
        else -> {}
    }
}

@Composable
fun PostScreen(
    navController: NavController,
    param: String?,
    postViewModel: PostViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        postViewModel(PostUiEvent.FetchSingle, param)
    }

    LazyColumn {
        item {
            Button(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("back")
            }
            when (val state = postViewModel.postUiState.collectAsStateWithLifecycle().value) {
                is PostUiState.Loading -> CircularProgressIndicator()
                is PostUiState.LoadedPost -> Text("${state.post}")
                is PostUiState.Error -> Text(state.message)
                else -> {}
            }
        }
    }
}