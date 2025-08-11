package com.example.apimelodixtfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apimelodixtfg.models.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {}
