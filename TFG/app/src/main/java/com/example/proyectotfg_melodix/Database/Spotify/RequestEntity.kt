package com.example.proyectotfg_melodix.Database.Spotify

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction

//Modelo de datos para las solicitudes
@Entity(tableName = "request_table")
data class RequestEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,    // PRIMARY, SECONDARY o BOTH
    val url: String
)

@Dao
interface RequestDao {
    // Obtener solicitudes por tipo
    @Query("SELECT * FROM request_table WHERE UPPER(type) = UPPER(:type)")
    suspend fun getRequestsByType(type: String): List<RequestEntity>

    // Obtener todas las relaciones de un ID secundario
    @Query("""
        SELECT r.*
        FROM request_table r
        INNER JOIN request_relations rr ON rr.secondaryRequestId = r.id
        WHERE rr.primaryRequestId = :primaryId
    """)
    suspend fun getSecondaryRequestsByPrimary(primaryId: String): List<RequestEntity>

    // Obtener todas las relaciones de un ID primario
    @Query("""
        SELECT r.*
        FROM request_table r
        INNER JOIN request_relations rr ON rr.primaryRequestId = r.id
        WHERE rr.secondaryRequestId = :secondaryId
    """)
    suspend fun getPrimaryRequestsBySecondary(secondaryId: String): List<RequestEntity>

    // Insertar solicitudes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<RequestEntity>)

    // Insertar relaciones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RequestRelationEntity>)

    // Obtener todas (para debug)
    @Query("SELECT * FROM request_table")
    suspend fun getAllRequests(): List<RequestEntity>
}



