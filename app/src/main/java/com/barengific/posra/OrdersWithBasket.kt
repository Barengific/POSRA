package com.barengific.posra

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.barengific.posra.basket.Basket
import com.barengific.posra.order.Orders

@Entity
data class OrdersWithBasket(
    @Embedded
    @ColumnInfo(name = "orderss") val orders: Orders,
    @Relation(
        parentColumn = "id",
        entityColumn = "OrderCreatedId"
    )
    @ColumnInfo(name = "basjket") val basket: List<Basket>
)