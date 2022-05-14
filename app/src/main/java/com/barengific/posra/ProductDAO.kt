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

    @Query("SELECT * FROM product WHERE id LIKE :k")
    fun findByID(k: String): List<Product>

    @Query("SELECT * FROM product WHERE barcode LIKE '%' || :k || '%'")
    fun findByBarcode(k: String): List<Product>

    @Query("SELECT * FROM product WHERE name LIKE '%' || :k || '%'")
    fun findByName(k: String): List<Product>

    @Query("SELECT * FROM product WHERE price LIKE '%' || :k || '%'")
    fun findByPrice(k: String): List<Product>

    @Query("SELECT * FROM product WHERE category LIKE '%' || :k || '%'")
    fun findByCategory(k: String): List<Product>

    @Query("SELECT * FROM product WHERE unit LIKE '%' || :k || '%'")
    fun findByUnit(k: String): List<Product>

    @Insert
    fun insertAll(vararg products: Product)

    @Delete
    fun delete(product: Product)
}