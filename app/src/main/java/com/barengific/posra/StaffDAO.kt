package com.barengific.posra

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StaffDAO {
    @Query("SELECT * FROM staff")
    fun getAll(): List<Staff>

    @Query("SELECT * FROM staff WHERE id IN (:staffIds)")
    fun loadAllByIds(staffIds: IntArray): List<Staff>

    @Query("SELECT * FROM staff WHERE id LIKE :k")
    fun findById(k: Int): Staff

    @Query("SELECT * FROM staff WHERE firstName LIKE :k")
    fun findByFirstName(k: String): Staff

    @Query("SELECT * FROM staff WHERE lastName LIKE :k")
    fun findByLastName(k: String): Staff

    @Query("SELECT * FROM staff WHERE email LIKE :k")
    fun findByEmail(k: String): Staff

    @Insert
    fun insertAll(vararg staff: Staff)

    @Delete
    fun delete(staff: Staff)
}
