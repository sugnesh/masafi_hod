package com.engear.vansales.localDB
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customersyncentity")
data class CustomerSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var payload: String,
    val customerCode: String,
    val customerName: String,
    var isSynced: Boolean = false
)
