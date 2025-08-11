package com.example.apimelodixtfg.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.apimelodixtfg.models.Playlist;
import com.example.apimelodixtfg.models.PlaylistSong;
import com.example.apimelodixtfg.models.Song;
import com.example.apimelodixtfg.repositories.PlaylistRepository;
import com.example.apimelodixtfg.repositories.PlaylistSongRepository;
import com.example.apimelodixtfg.repositories.SongRepository;

@RestController
@RequestMapping("/playlistsongs")
public class PlaylistSongController {

    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Autowired
    public PlaylistSongController(
        PlaylistSongRepository playlistSongRepository,
        PlaylistRepository playlistRepository,
        SongRepository songRepository
    ) {
        this.playlistSongRepository = playlistSongRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    // Obtener todas las relaciones playlist-canciones
    @GetMapping
    public List<PlaylistSong> getAllPlaylistSongs() {
        return playlistSongRepository.findAll();
    }

    // Añadir una canción a una playlist
    @PostMapping
    public PlaylistSong addSongToPlaylist(@RequestBody PlaylistSongRequest request) {
        Playlist playlist = playlistRepository.findById(request.getPlaylistId())
                .orElseThrow(() -> new RuntimeException("Playlist no encontrada con id: " + request.getPlaylistId()));
        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new RuntimeException("Canción no encontrada con id: " + request.getSongId()));

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);

        return playlistSongRepository.save(playlistSong);
    }

    // Eliminar una relación por ID
    @DeleteMapping("/{id}")
    public String deletePlaylistSong(@PathVariable Long id) {
        Optional<PlaylistSong> playlistSongOptional = playlistSongRepository.findById(id);
        if (playlistSongOptional.isPresent()) {
            playlistSongRepository.deleteById(id);
            return "Canción eliminada de la playlist exitosamente.";
        } else {
            return "No se encontró relación con id: " + id;
        }
    }
}
