package com.barengific.posra.order

import androidx.room.*
import com.barengific.posra.OrdersWithBasket
import com.barengific.posra.basket.Basket
import com.barengific.posra.order.Orders

@Dao
interface OrdersDAO {
    @Query("SELECT * FROM orders")
    fun getAll(): List<Orders>

    @Query("SELECT * FROM orders WHERE id IN (:orderIds)")
    fun loadAllByIds(orderIds: IntArray): List<Orders>

    @Query("SELECT * FROM orders WHERE id LIKE :k")
    fun findByID(k: String): List<Orders>

    @Query("SELECT * FROM orders WHERE date LIKE '%' || :k || '%'")
    fun findByDate(k: String): List<Orders>

    @Query("SELECT * FROM orders WHERE totalPrice LIKE '%' || :k || '%'")
    fun findByPrice(k: String): List<Orders>

//    @Transaction
//    @Query("UPDATE orders SET date = :date, totalPrice = :totalPrice, totalDiscount = :totalDiscount, " +
//            "totalQty = :totalQty, itemList = :itemList WHERE id = :i")
//    fun update(i: String, date: String, totalPrice: String, totalDiscount: String, totalQty: String, itemList: Basket)

    @Update
    fun updateOrder(orders: Orders?)

    @Insert
    fun insertAll(vararg orders: Orders)

    @Delete
    fun delete(orders: Orders)

    @Transaction
    @Query("SELECT * FROM orders")
    fun getUsersWithPlaylists(): List<OrdersWithBasket>
}