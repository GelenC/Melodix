package com.example.apimelodixtfg.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.apimelodixtfg.models.Playlist;
import com.example.apimelodixtfg.repositories.PlaylistRepository;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    // Obtener todas las playlists
    @GetMapping
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    // Crear nueva playlist
    @PostMapping
    public Playlist createPlaylist(@RequestBody Playlist playlist) {
        return playlistRepository.save(playlist);
    }

 // Actualizar el nombre de una playlist
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePlaylistName(@PathVariable Long id, @RequestBody Playlist updatedPlaylist) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(id);
        if (optionalPlaylist.isPresent()) {
            Playlist playlist = optionalPlaylist.get();
            playlist.setName(updatedPlaylist.getName());
            playlistRepository.save(playlist);
            return ResponseEntity.ok("Nombre de la playlist actualizado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Playlist no encontrada.");
        }
    }


 // Eliminar una playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlaylist(@PathVariable Long id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return ResponseEntity.ok("Playlist eliminada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Playlist no encontrada.");
        }
    }

}

