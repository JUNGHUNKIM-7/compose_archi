# conditional modifier

```kotlin
Modifier.then {
    if (cond) {

    } else {

    }
}
```

# passing pointer from hoisting level

```kotlin
//parent
val clickCount by remember { mutableStateOf(0) }


//child
fun SnackBarCaller(
    clickCount: Int
) {
    var clickCount1 = clickCount // share reference
}
```

# modifier

```
Text
modifier.paddingFromBaseline(top = 24.dp, bottom = 16.dp)

Row / Col
modifier.padding() // padding wrapper

Image
modifier.size(88.dp).clip(CircleShape)


//make page scrollable
val scrollableState = rememberScrollState()
Column(
    modifier = Modifier.padding(pv).fillMaxSize().verticalScroll(
        state = scrollableState
    ), horizontalAlignment = Alignment.CenterHorizontally
)

```

# resource

```
stringResource(text).uppercase(java.util.Locale.getDefault())
painterResource(drawable)
```

# lazy modifier

```
Row / Col
modifier = Modifier.padding(horizontal = 16.dp) // this padding for box
horizontalArrangement = Arrangement.spacedBy(8.dp) // elem spacing
contentPadding = PaddingValues(horizontal = 16.dp) // padding for content

Grid
horizontalArrangement = Arrangement.spacedBy(8.dp)
verticalArrangement = Arrangement.spacedBy(8.dp)
contentPadding = PaddingValues(horizontal = 8.dp)
```

# sizing

```
heightIn(min, max) //minimum
modifier = modifier.heightIn(min = 56.dp).fillMaxWidth() //fill or flex - fill..(0.5f)
```

# simple widget

- component itself is Unit -> Not value type

```kotlin
Surface(
    shape = MaterialTheme.shapes.small,
    modifier = modifier.background(Color.Cyan)
)

Scaffold(
    topBar = {
        SmallTopAppBar(
            title = { Text("title") }
        )
    },
    bottomBar = { BottomNav(navController) }
) { pv ->
    Box(modifier = Modifier.padding(pv)) {
        Column {
            //body here
        }
    }
}


Text(
    stringResource(R.string.app_name),
    style = MaterialTheme.typography.bodySmall,
)


Spacer(modifier = Modifier.padding(vertical = 16.dp))

// center
Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
}

LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {}
```

# nested

- no composable annotation for extension function!

```kotlin
LazyColumn(modifier = Modifier.fillMaxSize()) {
    Body()
    Initialize(dataList)
}

fun LazyListScope.Body(
) {
    item {
//        Compose1..Compose2..
    }
}

fun LazyListScope.Initialize(dataList: List<Post>) {
    items(dataList.size) {
        //
    }
}

```

# font/image

```kotlin
//res/font/fira_xxx.ttf
val firaSansFamily = FontFamily(
    Font(R.font.firasans_light, FontWeight.Light),
    Font(R.font.firasans_regular, FontWeight.Normal),
    Font(R.font.firasans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.firasans_medium, FontWeight.Medium),
    Font(R.font.firasans_bold, FontWeight.Bold)
)

Text(text = "text", fontFamily = firaSansFamily, fontWeight = FontWeight.Light)

Image(
    painterResource(R.drawable.ic_launcher_foreground),
    contentDescription = null,
    contentScale = ContentScale.Fit,
)

// implementation("io.coil-kt:coil-compose:2.4.0")
AsyncImage(
    modifier = Modifier.size(150.dp).clip(CircleShape),
    model = "https://source.unsplash.com/random",
    contentDescription = null,
    contentScale = ContentScale.Crop
)
```

# inputs

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    maxLines = 1,
    value = text,
    onValueChange = { text = it },
)

//colors
colors = TextFieldDefaults.textFieldColors(containerColor = Color.Blue)

//validation
```

# scroll control

```kotlin
rememberLazyListState()

// val LazyListState.isScoroll = if ().. else ..
```

# handle focus

```kotlin
val focus = LocalFocusManager.current
Button(
    onClick = {
        focus.clearFocus()
        //todo: make text value to ""
        //todo: api request..
    }
) {
    Text("Reset")
}
```

# lazy

```kotlin
LazyHorizontalGrid(
    rows = GridCells.Fixed(2),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 8.dp),
) {
    items(10) {
        FavCollection()
    }
}
```

# navigator

```
implementation "androidx.navigation:navigation-compose:$nav_version"
```

```kotlin
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

@Composable
fun PostsScreen(
    navController: NavController,
    postViewModel: PostViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        postViewModel.invoke(PostUiEvent.Fetch)
    }

    when (val state = postViewModel.postsUiState.collectAsState().value) {
        is PostUiState.Loading -> CircularProgressIndicator()
        is PostUiState.LoadedPosts ->
            LazyColumn {
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
        postViewModel.invoke(PostUiEvent.FetchSingle, param)
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
            when (val state = postViewModel.postUiState.collectAsState().value) {
                is PostUiState.Loading -> CircularProgressIndicator()
                is PostUiState.LoadedPost -> Text("${state.post}")
                is PostUiState.Error -> Text(state.message)
                else -> {}
            }
        }
    }
}


```

# snack bar

```kotlin
class SnackbarVisualsWithError(
    override val message: String,
    val isError: Boolean
) : SnackbarVisuals {
    override val actionLabel: String
        get() = if (isError) "Error" else "OK"
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Indefinite
}

val snackbarHostState = remember { SnackbarHostState() }

//pass snackbarHostState
SnackbarHost(snackbarHostState) { data ->
    val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
    val buttonColor = if (isError) {
        ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.error
        )
    } else {
        ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary
        )
    }

    Snackbar(
        modifier = Modifier
            .padding(12.dp),
        action = {
            TextButton(
                onClick = { if (isError) data.dismiss() else data.performAction() },
                colors = buttonColor
            ) { Text(data.visuals.actionLabel ?: "") }
        }
    ) {
        Text(data.visuals.message)
    }
}


// caller
val scope = rememberCoroutineScope()
var clickCount by remember { mutableStateOf(0) }

Button(
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                SnackbarVisualsWithError(
                    "Snackbar # ${++clickCount}",
                    isError = clickCount % 2 != 0
                )
            )
        }
    })
{
    Text("on")
}
```

# koin

```kotlin
// multiple module
viewModel { MyFirstViewModel(get()) }
viewModel { MySecondViewModel(get()) }

// multiple applying
modules(appModule, anotherModule)

val postModule = module {
    single { PostDataRepository(PostDataSource()) }
    viewModel { PostViewModel(get()) }

    //or
    viewModelOf(::PostViewModel)
}

startKoin {
    modules(postModule)
}


1.In composable arg
fun ScrollBody(
    repository: PostDataRepository = koinInject()
) {
}
2.setContent {
    val repository = koinInject<PostDataRepository>()
}

3.viewModel
@Composable
fun FetchButton(vm: PostViewModel = koinViewModel()) {
}

```

# collectAsStateWithLifecycle

```kotlin
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-beta01")
```

# serialize

```
//plugins
id 'org.jetbrains.kotlin.plugin.serialization' version '1.8.21'

//deps
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1'
```

# ktor client setup

```
// manifest
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

```kotlin
// def ktor_version = "2.3.2"
implementation "io.ktor:ktor-client-core:$ktor_version"
implementation "io.ktor:ktor-client-cio:$ktor_version"
implementation "io.ktor:ktor-client-content-negotiation:$ktor_version"
implementation "io.ktor:ktor-serialization-kotlinx-json:$ktor_version"

private val client = HttpClient(CIO) {
    install(ContentNegotiation) { //json to obj mapper
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
}
```

# vm

```kotiln
ds -> client + get + withContext  + post .. etc
repository -> result + flow + get stream
vm -> change uiState
view -> change view based on uiState
```

# koin

```kotlin
val postMod = module {
    singleOf(::PostDataSource)
    singleOf(::PostDataRepository)
    viewModelOf(::PostViewModel)
}

@Composable
fun PostsScreen(
    postDataRepository: PostDataRepository = koinInject(),
    postViewModel: PostViewModel = koinViewModel()
) {
}
```

# koin impl

```kotlin
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

@Composable
fun PostsScreen(
    postViewModel: PostViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        postViewModel.invoke(PostUiEvent.Fetch)
    }

    LazyColumn {
        item {
            when (val state = postViewModel.uiState.collectAsState().value) {
                is PostUiState.Loading -> CircularProgressIndicator()
                is PostUiState.LoadedPosts -> Text("${state.posts}")
                is PostUiState.Error -> Text(state.message)
                else -> {}
            }
        }
    }
}

@Composable
fun PostScreen(
    postViewModel: PostViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        postViewModel.invoke(PostUiEvent.FetchSingle, "10")
    }

    LazyColumn {
        item {
            when (val state = postViewModel.postUiState.collectAsState().value) {
                is PostUiState.Loading -> CircularProgressIndicator()
                is PostUiState.LoadedPost -> Text("${state.post}")
                is PostUiState.Error -> Text(state.message)
                else -> {}
            }
        }
    }
}

```

# Post request

```kotlin
scope.launch {
    val post = Post.getInstance(valueMap) // map to post instance
    val result = KtorClient.postPost(post)
    when (result.isSuccess) {
        true -> println(result.getOrDefault(SuccessT.PostOk("okay")).message)
        else -> println(result.exceptionOrNull()?.message)
    }
}

suspend fun postPost(post: Post): Result<SuccessT.PostOk> =
    withContext(Dispatchers.IO) {
        val res = client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(post)
        }
        if (res.status == HttpStatusCode.Created) {
            Result.success(SuccessT.PostOk("okay"))
        } else {
            Result.failure(Throwable(ErrorT.PostErr.toString()))
        }
    }
```
