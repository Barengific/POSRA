package com.barengific.posra

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.MainActivity.Companion.recyclerView
import com.barengific.posra.databinding.AddproductLayoutBinding
import com.google.accompanist.permissions.rememberPermissionState
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class AddProduct : AppCompatActivity() {
    private lateinit var binding: AddproductLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddproductLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db initialise
        val passphrase: ByteArray = SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        val room = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")
            .openHelperFactory(factory)
            .allowMainThreadQueries()
            .build()
        val productDAO = room.productDao()

        //recycle view
        val arr = productDAO.getAll()
        val adapter = CustomAdapter(arr)
        recyclerView = findViewById<View>(R.id.rView) as RecyclerView
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    private fun manageUsers(){

    }
    private fun serverCustomer(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}