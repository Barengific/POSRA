package com.barengific.posra.order

import androidx.room.*
import com.barengific.posra.basket.Basket

@Entity
data class Orders (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "totalPrice") var totalPrice: Double?,
    @ColumnInfo(name = "totalDiscount") var totalDiscount: Double?,
    @ColumnInfo(name = "totalQty") var totalQty: Int?,
    @TypeConverters(Basket::class)
    @ColumnInfo(name = "itemList") var itemList: MutableList<Basket>?
    )
