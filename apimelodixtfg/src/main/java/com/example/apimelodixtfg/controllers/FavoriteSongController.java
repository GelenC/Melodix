package com.example.apimelodixtfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.apimelodixtfg.models.FavoriteSong;
import com.example.apimelodixtfg.models.Song;
import com.example.apimelodixtfg.repositories.FavoriteSongRepository;
import com.example.apimelodixtfg.repositories.SongRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/favorites/songs")
public class FavoriteSongController {

    private final FavoriteSongRepository favoriteSongRepository;
    private final SongRepository songRepository;

    @Autowired
    public FavoriteSongController(
        FavoriteSongRepository favoriteSongRepository,
        SongRepository songRepository
    ) {
        this.favoriteSongRepository = favoriteSongRepository;
        this.songRepository = songRepository;
    }

    @GetMapping
    public List<FavoriteSong> getAllFavoriteSongs() {
        return favoriteSongRepository.findAll();
    }

    @PostMapping
    public FavoriteSong addFavoriteSong(@RequestBody FavoriteSongRequest request) {
        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new RuntimeException("Canción no encontrada con id: " + request.getSongId()));

        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setSong(song);

        return favoriteSongRepository.save(favoriteSong);
    }

    @DeleteMapping("/{id}")
    public String deleteFavoriteSong(@PathVariable Long id) {
        Optional<FavoriteSong> favoriteSongOptional = favoriteSongRepository.findById(id);
        if (favoriteSongOptional.isPresent()) {
            favoriteSongRepository.deleteById(id);
            return "Canción favorita eliminada exitosamente.";
        } else {
            return "No se encontró una canción favorita con id: " + id;
        }
    }
}

