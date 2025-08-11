package com.example.apimelodixtfg.controllers;

public class PlaylistSongRequest {
    private Long playlistId;
    private String songId;

    // Constructor vacío
    public PlaylistSongRequest() {}

    // Getters y Setters
    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}

