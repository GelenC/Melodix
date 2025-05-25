package com.example.proyectotfg_melodix.Database.Spotify

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

//Modelo para gestionar el Token
@Entity(tableName = "token_table")
data class TokenEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long, // Duración en segundos
    val timestamp: Long // Momento en que se guardó el token
)

// Interfaz DAO para gestionar la base de datos.
@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateToken(token: TokenEntity)

    @Query("SELECT * FROM token_table LIMIT 1")
    suspend fun getToken(): TokenEntity?

    @Query("DELETE FROM token_table")
    suspend fun deleteToken()
}

//Repositorio para manejar las operaciones de Room
class TokenRepository(private val tokenDao: TokenDao) {
    suspend fun getToken(): TokenEntity? {
        return tokenDao.getToken()
    }
    suspend fun saveToken(token: TokenEntity) {
        tokenDao.insertOrUpdateToken(token)
    }
    suspend fun deleteToken() {
        tokenDao.deleteToken()
    }
    suspend fun isTokenExpired(): Boolean {
        val token = tokenDao.getToken() ?: return true
        val currentTime = System.currentTimeMillis() / 1000 // Tiempo actual en segundos
        return (currentTime - token.timestamp) >= token.expiresIn
    }
}

