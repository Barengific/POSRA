package com.barengific.posra

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Product (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "barcode") val pType: String?,
    @ColumnInfo(name = "name") var key: String?,
    @ColumnInfo(name = "description") var value: String?,
    @ColumnInfo(name = "price") var value: String?,
    @ColumnInfo(name = "price") var value: String?
    )