package com.example.apimelodixtfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apimelodixtfg.models.FavoriteSong;

public interface FavoriteSongRepository extends JpaRepository<FavoriteSong, Long> {}
