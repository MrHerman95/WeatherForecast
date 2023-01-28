package com.hermanbocharov.weatherforecast.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class LocationEntity(
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "name_ru")
    val nameRu: String?,
    @ColumnInfo(name = "name_uk")
    val nameUk: String?,
    val lat: Double,
    val lon: Double,
    @ColumnInfo(name = "country_code")
    val countryCode: String,
    val state: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}