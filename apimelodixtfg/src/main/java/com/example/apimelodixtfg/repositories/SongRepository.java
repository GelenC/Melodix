package com.example.apimelodixtfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 

import com.example.apimelodixtfg.models.Song;

public interface SongRepository extends JpaRepository<Song, String> {}



