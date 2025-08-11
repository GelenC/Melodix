package com.example.apimelodixtfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 

import com.example.apimelodixtfg.models.PlaylistSong;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {}
