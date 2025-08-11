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

import com.example.apimelodixtfg.models.Song;
import com.example.apimelodixtfg.repositories.SongRepository;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongRepository songRepository;

    @Autowired
    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // Obtener todas las canciones
    @GetMapping
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // Insertar una nueva canción
    @PostMapping
    public Song createSong(@RequestBody Song song) {
        return songRepository.save(song);
    }

    // Eliminar una canción por ID
    @DeleteMapping("/{id}")
    public String deleteSong(@PathVariable String id) {
        Optional<Song> songOptional = songRepository.findById(id);
        if (songOptional.isPresent()) {
            songRepository.deleteById(id);
            return "Canción eliminada exitosamente.";
        } else {
            return "Canción no encontrada con id: " + id;
        }
    }
}
