package com.barengific.posra

import androidx.room.*
import com.barengific.posra.order.Orders

@Dao
interface OrdersWithBasketDAO {

    @Transaction
    @Query("SELECT * FROM ordersWithBasket")
    fun getUsersWithPlaylists(): List<OrdersWithBasket>
}