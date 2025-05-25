package com.example.proyectotfg_melodix.ViewModel.Player

import androidx.lifecycle.ViewModel
import com.example.proyectotfg_melodix.Model.Player.PlaybackMode
import com.example.proyectotfg_melodix.Model.Player.PlayerState
import com.example.proyectotfg_melodix.Model.Player.PlayerStatus
import com.example.proyectotfg_melodix.Model.Request.Song
import com.example.proyectotfg_melodix.Retrofit.Youtube.YouTubeRepository
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(private val youTubeRepository: YouTubeRepository) : ViewModel() {

    private var youTubePlayer: YouTubePlayer? = null

    private val _playerStatus = MutableStateFlow(PlayerStatus.IDLE)
    val playerStatus: StateFlow<PlayerStatus> = _playerStatus

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    private val _duration = MutableStateFlow(0f)
    val duration: StateFlow<Float> = _duration

    private val _currentTime = MutableStateFlow(0f)
    val currentTime: StateFlow<Float> = _currentTime

    private val _playbackMode = MutableStateFlow(PlaybackMode.PLAY_NEXT)
    val playbackMode: StateFlow<PlaybackMode> = _playbackMode

    fun togglePlaybackMode() {
        _playbackMode.value = when (_playbackMode.value) {
            PlaybackMode.PLAY_NEXT -> PlaybackMode.REPEAT_ONE
            PlaybackMode.REPEAT_ONE -> PlaybackMode.STOP_AT_END
            PlaybackMode.STOP_AT_END -> PlaybackMode.PLAY_NEXT
        }
    }
    fun updateCurrentSong(updatedSong: Song) {
        _playerState.value = _playerState.value.copy(currentSong = updatedSong)
    }

    fun playFromYouTube(videoId: String, song: Song) {
        _playerStatus.value = PlayerStatus.LOADING
        _playerState.value = PlayerState(currentSong = song, currentVideoId = videoId, isPlaying = true)
    }

    fun setYouTubePlayer(player: YouTubePlayer) {
        youTubePlayer = player
    }

    fun setStatus(status: PlayerStatus) {
        _playerStatus.value = status
    }

    fun setDuration(duration: Float) {
        _duration.value = duration
    }

    fun updateCurrentTime(current: Float) {
        _currentTime.value = current
    }

    fun seekTo(seconds: Float) {
        youTubePlayer?.seekTo(seconds)
    }

    fun loadAndPlay(videoId: String) {
        youTubePlayer?.loadVideo(videoId, 0f)
        _playerStatus.value = PlayerStatus.PLAYING
        _currentTime.value = 0f
    }

    fun play() {
        youTubePlayer?.play()
        _playerStatus.value = PlayerStatus.PLAYING
    }

    fun pause() {
        youTubePlayer?.pause()
        _playerStatus.value = PlayerStatus.PAUSED
    }

    fun stop() {
        youTubePlayer?.pause()
        youTubePlayer?.seekTo(0f)
        _playerStatus.value = PlayerStatus.IDLE
        _currentTime.value = 0f
    }

    fun restart() {
        youTubePlayer?.seekTo(0f)
        youTubePlayer?.play()
        _playerStatus.value = PlayerStatus.PLAYING
        _currentTime.value = 0f
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }

    fun playFromPlaylist(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty() || startIndex !in songs.indices) return
        val song = songs[startIndex]
        _playerStatus.value = PlayerStatus.LOADING
        CoroutineScope(Dispatchers.IO).launch {
            val videoId = youTubeRepository.searchFirstVideoId("${song.title} ${song.artist}")
            videoId?.let {
                withContext(Dispatchers.Main) {
                    _playerState.value = PlayerState(
                        currentSong = song,
                        currentVideoId = it,
                        isPlaying = true,
                        playlist = songs,
                        currentIndex = startIndex
                    )
                    loadAndPlay(it)
                }
            }
        }
    }

    fun playNextInPlaylist() {
        val playlist = _playerState.value.playlist ?: return
        if (playlist.size <= 1) return // No tiene sentido avanzar
        val nextIndex = (_playerState.value.currentIndex + 1) % playlist.size
        playFromPlaylist(playlist, nextIndex)
    }

    fun playPreviousInPlaylist() {
        val playlist = _playerState.value.playlist ?: return
        if (playlist.size <= 1) return // No tiene sentido retroceder
        val previousIndex = if (_playerState.value.currentIndex - 1 < 0)
            playlist.lastIndex else _playerState.value.currentIndex - 1
        playFromPlaylist(playlist, previousIndex)
    }

}
