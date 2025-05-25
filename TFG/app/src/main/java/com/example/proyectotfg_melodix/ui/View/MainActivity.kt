package com.example.proyectotfg_melodix.ui.View

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.proyectotfg_melodix.Database.Spotify.AppDatabase
import com.example.proyectotfg_melodix.Database.Spotify.TokenRepository
import com.example.proyectotfg_melodix.Model.Request.ArtistItem as RawArtistItem
import com.example.proyectotfg_melodix.Model.Request.ContentItem
import com.example.proyectotfg_melodix.Model.Request.Song
import com.example.proyectotfg_melodix.Retrofit.Request.SpotifyApiService
import com.example.proyectotfg_melodix.Retrofit.Request.SpotifyRepository
import com.example.proyectotfg_melodix.ViewModel.Player.PlayerViewModel
import com.example.proyectotfg_melodix.ViewModel.Request.ContentExplorerViewModel
import com.example.proyectotfg_melodix.ViewModel.Token.SpotifyAuthViewModel
import com.example.proyectotfg_melodix.ui.theme.ProyectoTFG_MelodixTheme
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectotfg_melodix.BuildConfig
import com.example.proyectotfg_melodix.Model.Player.PlayerStatus
import com.example.proyectotfg_melodix.Model.Request.Track
import com.example.proyectotfg_melodix.Retrofit.Youtube.YouTubeRepository
import com.example.proyectotfg_melodix.ViewModel.MelodixApi.MelodixViewModel
import com.example.proyectotfg_melodix.ViewModel.Search.SearchViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.proyectotfg_melodix.Model.MelodixApi.Playlist
import com.example.proyectotfg_melodix.Model.MelodixApi.PlaylistWithSongs
import com.example.proyectotfg_melodix.Model.Player.PlaybackMode
import com.example.proyectotfg_melodix.R
import com.example.proyectotfg_melodix.Retrofit.MelodixApi.MelodixApiService
import com.example.proyectotfg_melodix.Retrofit.MelodixApi.MelodixRepository
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoTFG_MelodixTheme {
                MainScreen()
            }
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", Icons.Default.Home),
        BottomNavItem("buscar", Icons.Default.Search),
        BottomNavItem("playlist", Icons.Default.LibraryMusic),
        BottomNavItem("favorites/songs", Icons.Default.Favorite),
        BottomNavItem("favorites/artists", Icons.Default.Person)
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF5120AE), Color(0xFF214EF3)) // morado a azul eléctrico
    )

    // Fondo visual sin padding aquí
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = gradientBrush)
    ) {
        // Aquí SÍ se aplica el padding del sistema
        BottomNavigation(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (currentRoute == item.route) Color.White else Color.LightGray.copy(alpha = 0.7f)
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = false
                                popUpTo("home") { saveState = true }
                            }
                        }
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }
}


//Campo de busqueda de Canciones: query -> Lo escrito, onQueryChange -> cada que cambia, onSearchClick -> Cuando se presiona el botón
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    enabled: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00FFFF), // celeste
            Color(0xFF8A2BE2)  // violeta
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = {
                Text("Buscar canción o artista...", color = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(56.dp)
                .background(brush = gradientBrush, shape = CircleShape)
                .clickable { onSearchClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.White
            )
        }
    }
}


//Administrar la navegación
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    val tokenRepository = remember { TokenRepository(database.tokenDao()) }
    val authViewModel = remember { SpotifyAuthViewModel(tokenRepository) }
    val isTokenReady by authViewModel.isTokenReady.collectAsState()

    val youTubeRepository = remember { YouTubeRepository(BuildConfig.YOUTUBE_API_KEY) }
    val playerViewModel = remember { PlayerViewModel(youTubeRepository) }

    LaunchedEffect(Unit) {
        authViewModel.checkAndFetchToken(
            clientId = "Your_clientId",
            clientSecret = "Your_clientSecret"
        )
    }

    if (!isTokenReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val spotifyApiService = remember { SpotifyApiService.create() }
        val repository = remember {
            SpotifyRepository(
                tokenRepository = tokenRepository,
                requestDao = database.requestDao(),
                spotifyApiService = spotifyApiService
            )
        }


        val searchViewModel: SearchViewModel = remember { SearchViewModel(youTubeRepository, repository) }

        val melodixApiService:MelodixApiService = remember { MelodixApiService.create() }
        val melodixRepository:MelodixRepository = remember { MelodixRepository(melodixApiService)}
        val melodixViewModel = remember { MelodixViewModel(melodixRepository = melodixRepository) }
        val contentViewModel = remember { ContentExplorerViewModel(repository, melodixRepository) }



        //Estático en la pantalla
        Scaffold(
            bottomBar = { BottomNavBar(navController) },
            modifier = Modifier.fillMaxSize()

        ) { paddingValues ->

            LocalBackgroundPlayer()

            Box(modifier = Modifier.padding(paddingValues)) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {

                            // Explorador de contenido principal
                            ContentExplorer(
                                navController = navController,
                                viewModel = contentViewModel,
                                searchViewModel = searchViewModel,
                                onSongClick = { selectedSong ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val videoId = youTubeRepository.searchFirstVideoId("${selectedSong.title} ${selectedSong.artist}")
                                        videoId?.let {
                                            withContext(Dispatchers.Main) {
                                                playerViewModel.playFromYouTube(it, selectedSong)
                                                navController.navigate("player/$it")
                                            }
                                        }
                                    }
                                }
                            )
                    }

//RUTAS PROVISIONALES
                    composable("buscar") {
                        Search(
                            navController,
                            searchViewModel,
                            onSongClick = { song ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val videoId =
                                        youTubeRepository.searchFirstVideoId("${song.title} ${song.artist}")
                                    videoId?.let {
                                        withContext(Dispatchers.Main) {
                                            playerViewModel.playFromYouTube(it, song)
                                            navController.navigate("player/$it")
                                        }
                                    }
                                }
                            })
                    }
                    composable("playlist") {
                        PlaylistsScreen(
                            melodixViewModel, navController
                        )
                    }
                    composable("playlistDetails/{playlistId}") { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getString("playlistId")?.toIntOrNull()
                        playlistId?.let {
                            PlaylistDetailsScreen(
                                playlistId = it,
                                viewModel = melodixViewModel,
                                navController = navController,
                                onSongClick = { selectedSong ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val songs = melodixViewModel.selectedPlaylist.value?.songs?.map { it.song } ?: emptyList()
                                        val startIndex = songs.indexOfFirst { it.id == selectedSong.id }

                                        withContext(Dispatchers.Main) {
                                            playerViewModel.playFromPlaylist(songs, startIndex)
                                            navController.navigate("player")
                                        }
                                    }
                                }
                            )
                        }
                    }

                    composable("favorites/artists") {
                        ArtistFavoritesScreen(melodixViewModel, navController)
                    }
                    composable("favorites/songs") {
                        FavoriteSongsScreen(
                            viewModel = melodixViewModel,
                            navController = navController,
                            onSongClick = { song ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val videoId =
                                        youTubeRepository.searchFirstVideoId("${song.title} ${song.artist}")
                                    videoId?.let {
                                        withContext(Dispatchers.Main) {
                                            playerViewModel.playFromYouTube(it, song)
                                            navController.navigate("player/$it")
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable("player/{videoId}") {
                        PlayerScreen(
                            viewModel = playerViewModel,
                            melodixViewModel,
                            navController = navController,
                            contentViewModel
                        )
                    }
                    // Para reproducción desde playlist (sin necesidad de videoId)
                    composable("player") {
                        PlayerScreen(
                            viewModel = playerViewModel,
                            melodixViewModel,
                            navController = navController,
                            contentViewModel
                        )
                    }
                    composable("artistProfile/{artistId}") {
                        val artistId = it.arguments?.getString("artistId") ?: ""
                        ArtistProfileScreen(
                            artistId = artistId,
                            viewModel = contentViewModel,
                            melodixViewModel = melodixViewModel,
                            onSongClick = { song ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val videoId =
                                        youTubeRepository.searchFirstVideoId("${song.title} ${song.artist}")
                                    videoId?.let {
                                        withContext(Dispatchers.Main) {
                                            playerViewModel.playFromYouTube(it, song)
                                            navController.navigate("player/$it")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


//Vista inicial de exploración
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentExplorer(
    navController: NavController,
    viewModel: ContentExplorerViewModel,
    searchViewModel: SearchViewModel,
    onSongClick: (Song) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isSearchLoading by searchViewModel.isLoading.collectAsState()
    val sections by viewModel.contentSections.collectAsState()
    val listState = rememberLazyListState()

    val searchResults by searchViewModel.searchResults.collectAsState()
    val artistResults by searchViewModel.artistResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Estado del bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetVisible by remember { mutableStateOf(false) }


    // Contenido principal
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable {
                    isSheetVisible = true
                    scope.launch { bottomSheetState.show() }
                }
        ) {
            SearchBar(
                query = "",
                enabled = false,
                onQueryChange = {}, // no hace nada
                onSearchClick = {
                    isSheetVisible = true
                    scope.launch { bottomSheetState.show() }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && sections.isEmpty()) {
                // Mostrar loading en el centro de la pantalla
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {

                // Mostrar contenido normal
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    state = listState
                ) {
                    items(sections) { section ->
                        if (section.items.isNotEmpty()) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(section.items) { item ->
                                    when (item) {
                                        is ContentItem.SongItem -> SongCard(song = item.song) {
                                            onSongClick(item.song)
                                        }
                                        is ContentItem.ArtistItem -> ArtistCard(artist = item.artist) {
                                            navController.navigate("artistProfile/${item.artist.id}")
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                    if (isLoading && sections.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // Mostrar resultados de búsqueda en ModalBottomSheet, pantalla desplegable
            if (isSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        isSheetVisible = false
                        searchQuery = ""
                        searchViewModel.clearResults()
                        scope.launch { bottomSheetState.hide() }  //Se utiliza animación de esconder
                    },
                    sheetState = bottomSheetState,
                    containerColor = Color.Black
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                    ) {
                        // Barra de búsqueda dentro del BottomSheet
                        SearchBar(
                            query = searchQuery,
                            enabled = true,
                            onQueryChange = { newQuery ->
                                searchQuery = newQuery
                            },
                            onSearchClick = {
                                if (searchQuery.length >= 3) {
                                    searchViewModel.search(searchQuery)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isSearchLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            if (searchResults.isEmpty() && artistResults.isEmpty()) {
                                Text("Sin resultados")
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    // Sección de artistas (máx. 4, en 2 columnas)
                                    if (artistResults.isNotEmpty()) {
                                        item {
                                            Text(
                                                text = "Artistas",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }

                                        item {
                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(2),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(max = 400.dp) // Limita altura para evitar scroll dentro del grid
                                            ) {
                                                items(artistResults) { artist ->
                                                    Box(modifier = Modifier.fillMaxWidth()) {
                                                        ArtistCard(artist = artist) {
                                                            navController.navigate("artistProfile/${artist.id}")
                                                            scope.launch { bottomSheetState.hide() }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Sección de canciones
                                    if (searchResults.isNotEmpty()) {
                                        item {
                                            Text(
                                                text = "Canciones",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(vertical = 12.dp)
                                            )
                                        }

                                        items(searchResults) { track ->
                                            SearchResultCard(track = track) {
                                                val song = Song(
                                                    id = track.id,
                                                    title = track.name,
                                                    artist = track.artists.firstOrNull()?.name
                                                        ?: "Desconocido",
                                                    artistId = track.artists.firstOrNull()?.id
                                                        ?: "",
                                                    artistImageUrl = track.album.images.firstOrNull()?.url
                                                        ?: "",
                                                    imageUrl = track.album.images.firstOrNull()?.url
                                                        ?: "",
                                                    previewUrl = track.preview_url,
                                                    requestUrl = ""
                                                )
                                                onSongClick(song)
                                                scope.launch { bottomSheetState.hide() }
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }
    }


    // Carga progresiva
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItems - 3 && !isLoading) {
                    viewModel.loadMoreSections()
                }
            }
    }
}

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit
) {
    val neonGradients = listOf(
        listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
        listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
        listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde a azul
        listOf(Color(0xFFFF5623), Color(0xFFFF00FF))  // naranja a fucsia
    )
    val gradientBrush = remember(song.id) {
        Brush.linearGradient(neonGradients.random())
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.Black, RoundedCornerShape(16.dp))
            .border(5.dp, gradientBrush, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ArtistCard(
    artist: RawArtistItem,
    onClick: () -> Unit
) {
    val neonGradients = listOf(
        listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
        listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
        listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde a azul
        listOf(Color(0xFFFF5623), Color(0xFFFF00FF))  // naranja a fucsia
    )
    val colors = remember { neonGradients.random() }
    val backgroundBrush = remember { Brush.linearGradient(colors) }

    Box(
        modifier = Modifier
            .width(160.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = colors.first().copy(alpha = 0.5f),
                spotColor = colors.last().copy(alpha = 0.8f)
            )
            .background(brush = backgroundBrush, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = artist.images.firstOrNull()?.url ?: "",
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Text(
                        text = "Artista",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


@Composable
fun SearchResultCard(track: Track, onClick: () -> Unit) {
    val gradientColors = listOf(
        listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
        listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
        listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde neón a azul
        listOf(Color(0xFFFF5623), Color(0xFFFF00FF))  // naranja a fucsia
    )
    val borderGradient = remember { Brush.linearGradient(gradientColors.random()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            //.background(Color.Black, shape = RoundedCornerShape(16.dp))
            .border(
                width = 2.dp,
                brush = borderGradient,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url ?: "",
                contentDescription = track.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = track.name,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = track.artists.firstOrNull()?.name ?: "Artista desconocido",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}




@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    melodixViewModel: MelodixViewModel,
    navController: NavController,
    ContentViewModel: ContentExplorerViewModel
) {
    val status by viewModel.playerStatus.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    val song = playerState.currentSong
    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }

    val progress = if (duration > 0f) currentTime / duration else 0f

    val isFavorite by melodixViewModel.currentSongIsFavorite.collectAsState()

    var showPlaylistDialog by remember { mutableStateOf(false) }

    val isInPlaylist = playerState.playlist?.isNotEmpty() == true


    fun formatTime(seconds: Float): String {
        val mins = seconds.toInt() / 60
        val secs = seconds.toInt() % 60
        return "%02d:%02d".format(mins, secs)
    }

    LaunchedEffect(currentTime, isUserSeeking) {
        if (!isUserSeeking) {
            sliderPosition = progress
        }
    }

    LaunchedEffect(playerState.currentSong?.id) {
        playerState.currentSong?.id?.let { spotifyId ->
            melodixViewModel.checkIfSongIsFavorite(spotifyId)
        }
    }
    LaunchedEffect(Unit) {
        melodixViewModel.loadPlaylists() // Para tener las playlists al mostrar el diálogo
    }
//Para cuando la canción viene de la api (no se ve la imagen)
    if (song != null) {
        LaunchedEffect(song?.id, song?.artistImageUrl) {
            if (!song?.artistImageUrl.isNullOrBlank()) return@LaunchedEffect

            try {
                val token = ContentViewModel.getValidSpotifyToken()
                val artist = token?.let { ContentViewModel.getArtistInfo(song!!.artistId, it) }
                val imageUrl = artist?.images?.firstOrNull()?.url

                if (!imageUrl.isNullOrBlank()) {
                    val updated = song!!.copy(artistImageUrl = imageUrl)
                    viewModel.updateCurrentSong(updated)
                }
            } catch (e: Exception) {
                Log.e("PlayerScreen", "Error al cargar imagen del artista: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val neonGradients = listOf(
            listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
            listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
            listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde neón a azul
            listOf(Color(0xFFFF5623), Color(0xFFFF00FF))  // naranja a fucsia
        )
        val gradientColors = remember { neonGradients.random() }
        val gradientBrush = remember { Brush.linearGradient(gradientColors) }
        val shadowColor = gradientColors.first() // Puedes usar el primero del gradiente

        // Artista Imagen + Nombre + Navegación...
        if (song != null && song.artistImageUrl.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        navController.navigate("artistProfile/${song.artistId}")
                    }
                    .padding(bottom = 16.dp)
            ) {
                AsyncImage(
                    model = song.artistImageUrl,
                    contentDescription = song.artist,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(600.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .background(Color.Black, RoundedCornerShape(28.dp))
                .border(
                    width = 4.dp,
                    brush = gradientBrush,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(4.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.12f))
            ) {

                Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height((8.dp)))


                    // Imagen del álbum (el it hace referencia al mismo objeto; el AsyncImage descarga la imagen de la red)
                    song?.imageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = when (status) {
                            PlayerStatus.LOADING -> "Cargando..."
                            PlayerStatus.PLAYING -> "Reproduciendo"
                            PlayerStatus.PAUSED -> "Pausado"
                            PlayerStatus.ERROR -> "Error al reproducir"
                            PlayerStatus.IDLE -> "Listo"
                        },
                        color = if (status == PlayerStatus.ERROR) Color.Red else Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = if (isInPlaylist) "Modo lista de reproducción" else "Modo canción individual",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00FFFF)
                    )


                    Spacer(modifier = Modifier.height(16.dp))

            //Botones para guardar y favoritos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                playerState.currentSong?.let {
                                    melodixViewModel.toggleFavoriteInPlayer(
                                        it
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(onClick = { showPlaylistDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.LibraryAdd, // o cualquier ícono de "guardar"
                                contentDescription = "Añadir a playlist",
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val displayTime =
                            if (isUserSeeking) sliderPosition * duration else currentTime
                        Text(formatTime(displayTime), color = Color.White)
                        Text(formatTime(duration), color = Color.White)
                    }

                    val neonThumbColor = remember {
                        Brush.linearGradient(
                            listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)) // Fucsia a celeste (o el que uses)
                        )
                    }
                    val neonThumbColorApprox = Color(0xFFDD00FF) // Representa el gradiente

                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            isUserSeeking = true
                        },
                        onValueChangeFinished = {
                            viewModel.seekTo(sliderPosition * duration)
                            isUserSeeking = false
                        },
                        modifier = Modifier.fillMaxWidth(0.9f),
                        enabled = status != PlayerStatus.LOADING && duration > 0f,
                        colors = SliderDefaults.colors(
                            thumbColor = neonThumbColorApprox,
                            activeTrackColor = neonThumbColorApprox,
                            inactiveTrackColor = neonThumbColorApprox.copy(alpha = 0.3f)
                        )
                    )

                    val mode by viewModel.playbackMode.collectAsState()
                    IconButton(onClick = { viewModel.togglePlaybackMode() }) {
                        val icon = when (mode) {
                            PlaybackMode.PLAY_NEXT -> Icons.Default.QueueMusic
                            PlaybackMode.REPEAT_ONE -> Icons.Default.RepeatOne
                            PlaybackMode.STOP_AT_END -> Icons.Default.StopCircle
                        }
                        Icon(icon, contentDescription = "Modo de reproducción", tint = Color.White)
                    }

//Botones para manejar el reproductor
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isInPlaylist) {
                            IconButton(onClick = { viewModel.playPreviousInPlaylist() }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior", tint = Color.White, modifier = Modifier.size(36.dp))
                            }

                            IconButton(onClick = {
                                if (status == PlayerStatus.PLAYING) viewModel.pause()
                                else viewModel.play()
                            }) {
                                Icon(
                                    imageVector = if (status == PlayerStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            IconButton(onClick = { viewModel.playNextInPlaylist() }) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente", tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                        } else {
                            // Siempre mostrar reiniciar a la izquierda
                            IconButton(onClick = { viewModel.restart() }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Reiniciar", tint = Color.White, modifier = Modifier.size(36.dp))
                            }

                            // Mostrar el botón activo (play o pausa) en el centro
                            IconButton(onClick = {
                                if (status == PlayerStatus.PLAYING) viewModel.pause()
                                else viewModel.play()
                            }) {
                                Icon(
                                    imageVector = if (status == PlayerStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            // Mostrar el botón inactivo (sin acción) al lado contrario
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = if (status == PlayerStatus.PLAYING) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

        if (showPlaylistDialog) {
            ChoosePlaylistDialog(
                playlists = melodixViewModel.playlists.value.map { Playlist(it.id, it.name, it.description) },
                onDismiss = { showPlaylistDialog = false },
                onSelect = { playlist ->
                    val currentSong = playerState.currentSong ?: return@ChoosePlaylistDialog
                    melodixViewModel.addCurrentSongToPlaylist(playlist.id, currentSong)
                    showPlaylistDialog = false
                }
            )
        }


        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    alpha = 0f
                    isEnabled = false
                    isClickable = false
                    isFocusable = false

                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

                        override fun onReady(player: YouTubePlayer) {
                            viewModel.setYouTubePlayer(player)
                            viewModel.playerState.value.currentVideoId?.let {
                                viewModel.loadAndPlay(it)
                            }
                        }

                        override fun onVideoDuration(player: YouTubePlayer, duration: Float) {
                            viewModel.setDuration(duration)
                        }

                        override fun onCurrentSecond(player: YouTubePlayer, second: Float) {
                            viewModel.updateCurrentTime(second)
                            if (second >= viewModel.duration.value - 0.5f) {
                                when (viewModel.playbackMode.value) {
                                    PlaybackMode.REPEAT_ONE -> player.seekTo(0f)
                                    PlaybackMode.PLAY_NEXT -> viewModel.playNextInPlaylist()
                                    PlaybackMode.STOP_AT_END -> viewModel.pause()
                                }
                            }
                        }

                        override fun onError(player: YouTubePlayer, error: PlayerConstants.PlayerError) {
                            viewModel.setStatus(PlayerStatus.ERROR)
                        }
                    })
                }
            },
            modifier = Modifier
                .size(1.dp)
                .pointerInput(Unit) {}
        )

    }
}

@Composable
fun ArtistProfileScreen(
    artistId: String,
    viewModel: ContentExplorerViewModel,
    melodixViewModel: MelodixViewModel,
    onSongClick: (Song) -> Unit = {}
) {
    val artistSongs by viewModel.artistSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val artist by viewModel.selectedArtist.collectAsState()
    val gridState = rememberLazyGridState()

    val selectedArtistfavorite by melodixViewModel.selectedArtistFavorite.collectAsState()

    LaunchedEffect(artistId) {
        viewModel.resetArtistSongs()
        viewModel.loadArtistTopTracks(artistId)
        melodixViewModel.checkIfFavorite(artistId)
    }

    Box(modifier = Modifier.fillMaxSize()
        //.background(Color.Black)
    ) {
        if (isLoading && artistSongs.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Cabecera con imagen y nombre
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            artist?.images?.firstOrNull()?.url?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = artist?.name ?: "",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = artist?.name ?: "Artista",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 30.sp,
                                color = Color.White
                            )
                            IconButton(
                                onClick = {
                                    artist?.let { melodixViewModel.toggleFavorite(it) }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedArtistfavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63),
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }
                    }

                    // Canciones
                    items(artistSongs.size) { index ->
                        val song = artistSongs[index]
                        val neonColors = listOf(
                            Color.Cyan, Color.Magenta,
                            Color(0xFF94FF38), Color(0xFFFF00FF),
                            Color(0xFF00FFFF), Color(0xFFFF5623),
                            Color(0xFF214EF3)
                        )
                        val gradientColors = remember(song.id) {
                            neonColors.shuffled().take(2)
                        }
                        val gradientBrush = Brush.linearGradient(gradientColors)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = gradientColors.first().copy(alpha = 0.6f),
                                    spotColor = gradientColors.last().copy(alpha = 0.8f)
                                )
                                .border(
                                    width = 2.dp,
                                    brush = gradientBrush,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(Color.Black, shape = RoundedCornerShape(16.dp))
                                .clickable { onSongClick(song) }
                                .padding(8.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = song.imageUrl,
                                    contentDescription = song.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = song.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }

            }

            // Scroll infinito
            LaunchedEffect(gridState) {
                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastIndex ->
                        val totalItems = gridState.layoutInfo.totalItemsCount
                        if (lastIndex != null && lastIndex >= totalItems - 3 && !isLoading) {
                            viewModel.loadMoreSongsForArtist(artistId)
                        }
                    }
            }
        }
    }
}

@Composable
fun Search(
    navController: NavController,
    searchViewModel: SearchViewModel,
    onSongClick: (Song) -> Unit) {

    var searchQuery by remember { mutableStateOf("") }
    val isSearchLoading by searchViewModel.isLoading.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val artistResults by searchViewModel.artistResults.collectAsState()

    LaunchedEffect(Unit) {
        searchQuery = ""
        searchViewModel.clearResults()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
           // .background(Color.Black)
    ) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
            //.background(Color.Black)
    ) {
        // Barra de búsqueda dentro del BottomSheet
        SearchBar(
            query = searchQuery,
            enabled = true,
            onQueryChange = { newQuery ->
                searchQuery = newQuery
            },
            onSearchClick = {
                if (searchQuery.length >= 3) {
                    searchViewModel.search(searchQuery)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isSearchLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            if (searchResults.isEmpty() && artistResults.isEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.busqueda),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.6f
                )
                //Text("Sin resultados", color = Color.White)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Sección de artistas (máx. 4, en 2 columnas)
                    if (artistResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Artistas",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.White
                            )
                        }

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp) // Limita altura para evitar scroll dentro del grid
                            ) {
                                items(artistResults) { artist ->
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        ArtistCard(artist = artist) {
                                            navController.navigate("artistProfile/${artist.id}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sección de canciones
                    if (searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Canciones",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color.White
                            )
                        }

                        items(searchResults) { track ->
                            SearchResultCard(track = track) {
                                val song = Song(
                                    id = track.id,
                                    title = track.name,
                                    artist = track.artists.firstOrNull()?.name
                                        ?: "Desconocido",
                                    artistId = track.artists.firstOrNull()?.id
                                        ?: "",
                                    artistImageUrl = track.album.images.firstOrNull()?.url
                                        ?: "",
                                    imageUrl = track.album.images.firstOrNull()?.url
                                        ?: "",
                                    previewUrl = track.preview_url,
                                    requestUrl = ""
                                )
                                onSongClick(song)
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
fun ArtistFavoritesScreen(viewModel: MelodixViewModel, navController: NavController) {
    val favoriteArtists by viewModel.favoriteArtists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val favoriteStates = remember { mutableStateMapOf<String, Boolean>() }
    favoriteArtists.forEach { artist ->
        if (favoriteStates[artist.id] == null) {
            favoriteStates[artist.id] = true // ya son favoritos
        }
    }

    val neonColors = listOf(
        Color.Cyan, Color.Magenta,
        Color(0xFF94FF38), // verde neón
        Color(0xFFFF00FF), // fucsia
        Color(0xFF00FFFF), // celeste
        Color(0xFFFF5623), // naranja neón
        Color(0xFF214EF3)  // azul eléctrico
    )

    // Gradientes únicos por artista
    val gradientMap = remember { mutableStateMapOf<String, Brush>() }
    favoriteArtists.forEach { artist ->
        if (gradientMap[artist.id] == null) {
            gradientMap[artist.id] = Brush.linearGradient(
                colors = listOf(neonColors.random(), neonColors.random())
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteArtists()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (favoriteArtists.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.no_favorite_artistsbackground),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.9f
            )
        }else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Artistas Favoritos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteArtists) { artistItem ->
                    // Colores únicos para cada artista
                    val colors = remember(artistItem.id) {
                        listOf(neonColors.random(), neonColors.random())
                    }

                    val gradientBrush = remember(artistItem.id) {
                        Brush.linearGradient(colors)
                    }

                    val isFavorite = favoriteStates[artistItem.id] == true

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = colors.first().copy(alpha = 0.6f),
                                spotColor = colors.last().copy(alpha = 0.8f)
                            )
                            .border(
                                width = 4.dp,
                                brush = gradientBrush,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .clickable {
                                navController.navigate("artistProfile/${artistItem.id}")
                            }
                            .padding(8.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = artistItem.images.firstOrNull()?.url,
                                contentDescription = artistItem.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = artistItem.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "Artista",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                IconButton(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    onClick = {
                                        viewModel.toggleFavorite(artistItem)
                                        favoriteStates[artistItem.id] = !isFavorite
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFE91E63),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
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
fun FavoriteSongsScreen(
    viewModel: MelodixViewModel,
    navController: NavController,
    onSongClick: (Song) -> Unit
) {
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val favoriteStates = remember { mutableStateMapOf<String, Boolean>() }
    favoriteSongs.forEach { song ->
        if (favoriteStates[song.id] == null) {
            favoriteStates[song.id] = true
        }
    }

    val neonColors = listOf(
        Color.Cyan, Color.Magenta,
        Color(0xFF94FF38), Color(0xFFFF00FF),
        Color(0xFF11191F), Color(0xFFFF5623),
        Color(0xFF214EF3)
    )

    // Map de gradientes únicos
    val gradientMap = remember { mutableStateMapOf<String, List<Color>>() }
    favoriteSongs.forEach { song ->
        if (gradientMap[song.id] == null) {
            val shuffled = neonColors.shuffled()
            gradientMap[song.id] = listOf(shuffled[0], shuffled[1])
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteSongs()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (favoriteSongs.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.favorite_songs_backround),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.8f // ajusta para que sea sutil
            )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Canciones Favoritas",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteSongs) { song ->
                    val colors = gradientMap[song.id] ?: listOf(Color.Cyan, Color.Magenta)
                    val gradientBrush = Brush.linearGradient(colors)
                    val isFavorite = favoriteStates[song.id] == true

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = colors.first().copy(alpha = 0.6f),
                                spotColor = colors.last().copy(alpha = 0.8f)
                            )
                            .border(
                                width = 4.dp,
                                brush = gradientBrush,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .clickable { onSongClick(song) }
                            .padding(8.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = song.imageUrl,
                                contentDescription = song.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = song.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                IconButton(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    onClick = {
                                        viewModel.toggleFavoriteSong(song)
                                        favoriteStates[song.id] = !isFavorite
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFE91E63),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//Pantallas de las Playlists
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    viewModel: MelodixViewModel,
    navController: NavController
) {
    val playlists by viewModel.playlists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<PlaylistWithSongs?>(null) }

    val gradientColors = listOf(
        listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
        listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
        listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde neón a azul
        listOf(Color(0xFFFF5623), Color(0xFFFF00FF)), // naranja a fucsia
        listOf(Color(0xFFFF5623), Color(0xFFFF00FF))
    )

    val gradientMap = remember { mutableStateMapOf<Int, Brush>() }

    playlists.forEach { playlist ->
        if (gradientMap[playlist.id] == null) {
            val colors = gradientColors.random()
            gradientMap[playlist.id] = Brush.linearGradient(colors)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPlaylists()
    }

    Box(modifier = Modifier.fillMaxSize()
        //.background(Color.Black)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (playlists.isEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.no_playlists_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.8f // ajusta para que sea sutil
                )

                //Text("No tienes playlists todavía.", color = Color.White)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        val background = gradientMap[playlist.id] ?: Brush.linearGradient(
                            listOf(Color.DarkGray, Color.Black)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .background(background, shape = RoundedCornerShape(12.dp))
                                .combinedClickable(
                                    onClick = {
                                        navController.navigate("playlistDetails/${playlist.id}")
                                    },
                                    onLongClick = {
                                        showDeleteDialog = playlist
                                    }
                                )
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp)) // espacio superior opcional
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(
                                                bottomStart = 12.dp,
                                                bottomEnd = 12.dp
                                            )
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = playlist.name,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = playlist.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }
        }
        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Eliminar playlist") },
                text = { Text("¿Seguro que deseas eliminar esta playlist?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.deletePlaylist(showDeleteDialog!!.id)
                        showDeleteDialog = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva playlist")
        }
        if (showDialog) {
            CreatePlaylistDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, description ->
                    viewModel.createPlaylist(name, description)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Playlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddSongToPlaylistDialog(
    playlistId: Int,
    favoriteSongs: List<Song>,
    onAdd: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    Text(
                        "Recomendaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(favoriteSongs) { song ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSong = song }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSong == song,
                                    onClick = { selectedSong = song }
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                AsyncImage(
                                    model = song.imageUrl,
                                    contentDescription = song.title,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(song.title, fontWeight = FontWeight.SemiBold)
                                    Text(song.artist, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { selectedSong?.let { onAdd(it) } },
                            enabled = selectedSong != null
                        ) {
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Int,
    viewModel: MelodixViewModel,
    navController: NavController,
    onSongClick: (Song) -> Unit
) {
    val playlist by viewModel.selectedPlaylist.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedSongId by remember { mutableStateOf<String?>(null) }
    var selectedSongIdApi by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPlaylistById(playlistId)
        viewModel.loadFavoriteSongs()
    }

    Box(modifier = Modifier.fillMaxSize()
        //.background(Color.Black)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            playlist == null -> {
                Text(
                    text = "No se pudo cargar la playlist.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            playlist!!.songs.isEmpty() -> {
                Text(
                    text = "Esta playlist aún no tiene canciones.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlist!!.songs) { item ->
                        val song = item.song
                        val playlistSongId = item.playlistSongId
                        val SongIdApi = item.song.idApi
                        val neonGradients = listOf(
                            listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2)), // celeste a violeta
                            listOf(Color(0xFFFF00FF), Color(0xFF00FFFF)), // fucsia a celeste
                            listOf(Color(0xFF94FF38), Color(0xFF214EF3)), // verde neón a azul
                            listOf(Color(0xFFFF5623), Color(0xFFFF00FF))  // naranja a fucsia // naranja neón a gris
                        )
                        val gradient = remember(song.id) {
                            Brush.linearGradient(neonGradients.random())
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                .border(
                                    width = 4.dp,
                                    brush = gradient,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .combinedClickable(
                                    onClick = { onSongClick(song) },
                                    onLongClick = {
                                        selectedSongId = playlistSongId.toString()
                                        showDeleteDialog = true
                                        selectedSongIdApi = SongIdApi
                                    }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = song.imageUrl,
                                    contentDescription = song.title,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                                ) {
                                    Column(Modifier.padding(start = 10.dp)) {
                                        Text(
                                            text = song.title,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = song.artist,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (showDeleteDialog && selectedSongId != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Eliminar canción") },
                        text = { Text("¿Estás seguro de que quieres eliminar esta canción de la playlist?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.removeSongFromPlaylist(selectedSongId!!.toInt(), selectedSongIdApi.toString())
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

            }
        }
        // FAB para añadir canción
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir canción")
        }

        // Diálogo de selección de canción favorita
        if (showAddDialog) {
            AddSongToPlaylistDialog(
                playlistId = playlistId,
                favoriteSongs = favoriteSongs,
                onAdd = { song ->
                    viewModel.addSongToPlaylist(playlistId, song)
                    viewModel.loadPlaylistById(playlistId)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
        }
    }
}
@Composable
fun ChoosePlaylistDialog(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelect: (Playlist) -> Unit
) {
    var selected by remember { mutableStateOf<Playlist?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona una playlist") },
        text = {
            Column {
                playlists.forEach { playlist ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { selected = playlist }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selected == playlist,
                            onClick = { selected = playlist }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(playlist.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selected?.let(onSelect) },
                enabled = selected != null
            ) { Text("Añadir") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun LocalBackgroundPlayer() {
    val context = LocalContext.current

    val originalVideoIds = listOf(
        R.raw.badbunnybokete,
        R.raw.camilofavorito,
        R.raw.despeinadacamiloozuna,
        R.raw.diluviorauwalejandro,
        R.raw.billieeilishbirdsofafeather,
        R.raw.billieeilishchiriro,
        R.raw.ladygagaabracadabra,
        R.raw.lisamoney

    )

    var currentIndex by remember { mutableStateOf(0) }
    var currentPlaylist by remember { mutableStateOf(originalVideoIds.shuffled()) }
    val videoKey = remember(currentIndex) { currentIndex }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(-1f)
    ) {
        key(videoKey) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        scaleX = 1.5f
                        scaleY = 3.8f

                        setVideoURI(getUriForRawResource(context, currentPlaylist[currentIndex]))
                        setZOrderOnTop(false)

                        setOnPreparedListener {
                            it.setVolume(0f, 0f)
                            it.start()
                        }

                        setOnCompletionListener {
                            val nextIndex = currentIndex + 1
                            if (nextIndex >= currentPlaylist.size) {
                                currentPlaylist = originalVideoIds.shuffled()
                                currentIndex = 0
                            } else {
                                currentIndex = nextIndex
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bloquea interacción
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
        )

        // Oscurecimiento opcional
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )
    }
}

fun getUriForRawResource(context: Context, @RawRes resId: Int): Uri {
    return Uri.parse("android.resource://${context.packageName}/$resId")
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoTFG_MelodixTheme {}
}