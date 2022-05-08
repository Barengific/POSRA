package com.barengific.posra

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDAO {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE id IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>

    @Query("SELECT * FROM product WHERE barcode LIKE :k")
    fun findByKey(k: String): Product

    @Insert
    fun insertAll(vararg products: Product)

    @Delete
    fun delete(product: Product)
}