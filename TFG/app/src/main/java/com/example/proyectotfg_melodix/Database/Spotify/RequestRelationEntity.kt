package com.example.proyectotfg_melodix.Database.Spotify

import androidx.room.Entity
import androidx.room.ForeignKey

//Modelos de datos para las relaciones entre solicitudes principales y secundarias
@Entity(
    tableName = "request_relations",
    primaryKeys = ["primaryRequestId", "secondaryRequestId"],
    foreignKeys = [
        ForeignKey(entity = RequestEntity::class, parentColumns = ["id"], childColumns = ["primaryRequestId"]),
        ForeignKey(entity = RequestEntity::class, parentColumns = ["id"], childColumns = ["secondaryRequestId"])
    ]
)
data class RequestRelationEntity(
    val primaryRequestId: String,
    val secondaryRequestId: String
)