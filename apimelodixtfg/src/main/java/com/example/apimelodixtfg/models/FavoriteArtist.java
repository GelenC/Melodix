package com.example.apimelodixtfg.models;

import jakarta.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//--- Artistas favoritos ---
@AllArgsConstructor
@Entity
@Table(name = "favorite_artists")
public class FavoriteArtist {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "spotify_artist_id")
    private String spotifyArtistId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

	public FavoriteArtist() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpotifyArtistId() {
		return spotifyArtistId;
	}

	public void setSpotifyArtistId(String spotifyArtistId) {
		this.spotifyArtistId = spotifyArtistId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    
}


