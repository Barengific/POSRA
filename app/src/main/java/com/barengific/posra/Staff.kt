package com.barengific.posra

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Staff (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "password") var password: String?,
    @ColumnInfo(name = "firstName") var firstName: String?,
    @ColumnInfo(name = "lastName") var lastName: String?,
    @ColumnInfo(name = "email") var email: String?,
    @ColumnInfo(name = "phoneNumber") var phoneNumber: String?,
    @ColumnInfo(name = "dateHired") var dateHired: String?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "jobTitle") var jobTitle: String?
)