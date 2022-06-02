package com.barengific.posra.basket

import androidx.room.*

@Entity
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val OrderCreatedId: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "price") var price: String?,
    @ColumnInfo(name = "qty") var qty: String?,
    @ColumnInfo(name = "total") var total: String?,
    @ColumnInfo(name = "barcode") var barcode: String?,
)