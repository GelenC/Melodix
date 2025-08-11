package com.example.apimelodixtfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apimelodixtfg.models.FavoriteArtist;

public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, String> {}
