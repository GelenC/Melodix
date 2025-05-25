package com.example.proyectotfg_melodix.Database.Spotify

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Database(
    entities = [TokenEntity::class, RequestEntity::class, RequestRelationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun requestDao(): RequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spotify_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    prepopulateDatabase(database) // Insertar las solicitudes y relaciones
                    Log.d("AppDatabase", "Prepoblación ejecutada")
                }
            }
        }


        suspend fun prepopulateDatabase(database: AppDatabase) {
            database.runInTransaction {
                val dao = database.requestDao()

                runBlocking {
                    val requests = listOf(
                        RequestEntity("genres_popular", "Explora por géneros 🎧", "SECONDARY", "/v1/browse/categories?limit=3"),
                        RequestEntity("top_artists_genre", "Artistas destacados en su estilo 🎵", "SECONDARY", "/v1/search?q=genre:pop&type=artist&limit=10"),
                        RequestEntity("top_tracks_genre", "Lo mejor del género ❤️‍🔥", "PRIMARY", "/v1/search?q=genre:pop&type=track&limit=10"),
                        RequestEntity("top_tracks_artist", "Éxitos de un artista 🪇", "PRIMARY", "/v1/artists/{artist_id}/top-tracks?market=US&offset=40"),
                        RequestEntity("top_tracks_10_artists", "Voces que marcan tendencia 🎤", "PRIMARY", "/v1/artists/{artist_id}/top-tracks?market=US&limit=1")
                    )

                    val relations = listOf(
                        RequestRelationEntity("top_tracks_artist", "top_artists_genre"), /* 2. Cambiar metodo utilizado (Se está utilizando el mismo que la relacion 2) y da valores incorrectos)*/
                        RequestRelationEntity("top_tracks_10_artists", "top_artists_genre"),  /* 1. Cambiar el genero al azar*/
                        RequestRelationEntity("top_tracks_genre", "genres_popular"), //3. Aparece pero sale siempre lo mismo
                        RequestRelationEntity("top_artists_genre", "genres_popular") // 4. No aparece nada
                    )


                    dao.insertRequests(requests)
                    dao.insertRelations(relations)
                }
            }
        }
    }
}
