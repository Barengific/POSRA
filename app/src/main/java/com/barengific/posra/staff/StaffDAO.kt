package com.barengific.posra.staff

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.barengific.posra.staff.Staff

@Dao
interface StaffDAO {
    @Query("SELECT * FROM staff")
    fun getAll(): List<Staff>

    @Query("SELECT * FROM staff WHERE id IN (:staffIds)")
    fun loadAllByIds(staffIds: IntArray): List<Staff>

    @Query("SELECT * FROM staff WHERE id LIKE :k")
    fun findById(k: Int): List<Staff>

    @Query("SELECT * FROM staff WHERE firstName LIKE :k")
    fun findByFirstName(k: String): List<Staff>

    @Query("SELECT * FROM staff WHERE lastName LIKE :k")
    fun findByLastName(k: String): List<Staff>

    @Query("SELECT * FROM staff WHERE email LIKE :k")
    fun findByEmail(k: String): List<Staff>

    @Query("SELECT * FROM staff WHERE phoneNumber LIKE :k")
    fun findByPhone(k: String): List<Staff>

    @Query("SELECT * FROM staff WHERE location LIKE :k")
    fun findByLocation(k: String): List<Staff>

    @Query("SELECT * FROM staff WHERE jobTitle LIKE :k")
    fun findByJob(k: String): List<Staff>

    @Insert
    fun insertAll(vararg staff: Staff)

    @Delete
    fun delete(staff: Staff)
}
