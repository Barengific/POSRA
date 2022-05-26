package com.barengific.posra.basket

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "price") var price: String?,
    @ColumnInfo(name = "qty") var qty: String?,
    @ColumnInfo(name = "total") var total: String?,
    @ColumnInfo(name = "barcode") var barcode: String?,
)