package com.barengific.posra

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.barengific.posra.basket.Basket
import com.barengific.posra.order.Orders
import com.barengific.posra.order.OrdersDAO
import com.barengific.posra.product.Product
import com.barengific.posra.product.ProductDAO
import com.barengific.posra.staff.Staff
import com.barengific.posra.staff.StaffDAO

//@TypeConverters(Basket::class)
@Database(entities = [Product::class, Staff::class, OrdersWithBasketDAO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private lateinit var INSTANCE:AppDatabase
        private lateinit var context: Context
        fun getInstance(con: Context):AppDatabase= Room.databaseBuilder(
            con,
            AppDatabase::class.java,
            "pos"
        )

            .createFromAsset("pos.db")
            .allowMainThreadQueries()
            .build()

    }

    abstract fun staffDao(): StaffDAO
    abstract fun productDao(): ProductDAO
    abstract fun ordersDao(): OrdersDAO
    abstract fun ordersWithBasketDAO(): OrdersWithBasketDAO

}