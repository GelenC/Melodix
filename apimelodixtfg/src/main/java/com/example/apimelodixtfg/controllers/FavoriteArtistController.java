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

import com.example.apimelodixtfg.models.FavoriteArtist;
import com.example.apimelodixtfg.repositories.FavoriteArtistRepository;

@RestController
@RequestMapping("/favorites/artists")
public class FavoriteArtistController {

    private final FavoriteArtistRepository favoriteArtistRepository;

    @Autowired
    public FavoriteArtistController(FavoriteArtistRepository favoriteArtistRepository) {
        this.favoriteArtistRepository = favoriteArtistRepository;
    }

    // Obtener todos los artistas favoritos
    @GetMapping
    public List<FavoriteArtist> getAllFavoriteArtists() {
        return favoriteArtistRepository.findAll();
    }

    // Añadir un artista a favoritos
    @PostMapping
    public FavoriteArtist addFavoriteArtist(@RequestBody FavoriteArtist favoriteArtist) {
        return favoriteArtistRepository.save(favoriteArtist);
    }

    // Eliminar un artista favorito por ID
    @DeleteMapping("/{id}")
    public String deleteFavoriteArtist(@PathVariable String id) {
        Optional<FavoriteArtist> favoriteArtistOptional = favoriteArtistRepository.findById(id);
        if (favoriteArtistOptional.isPresent()) {
            favoriteArtistRepository.deleteById(id);
            return "Artista favorito eliminado exitosamente.";
        } else {
            return "No se encontró un artista favorito con id: " + id;
        }
    }
}
