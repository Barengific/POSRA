package com.barengific.posra

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Product (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "barcode") var barcode: String?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "stockQty") var stockQty: String?,
    @ColumnInfo(name = "price") var price: String?,
    @ColumnInfo(name = "category") var category: String?,
    @ColumnInfo(name = "unit") var unit: String?,
    @ColumnInfo(name = "unit_as") var unit_as: String?
    )