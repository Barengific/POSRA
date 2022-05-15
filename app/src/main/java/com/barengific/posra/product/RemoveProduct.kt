package com.barengific.posra.product

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.AppDatabase
import com.barengific.posra.HomeActivity
import com.barengific.posra.Product
import com.barengific.posra.R
import com.barengific.posra.databinding.RemoveProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class RemoveProduct : AppCompatActivity() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            Companion.pos = pos
        }
        private var instance: RemoveProduct? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    private lateinit var binding: RemoveProductActivityBinding
    lateinit var bottomNav: BottomNavigationView


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RemoveProductActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNav.selectedItemId = R.id.nav_remove
//        bottomNav.setOnItemSelectedListener { menuItem ->
//            if (menuItem.itemId == R.id.nav_home) {
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                false
//            } else if (menuItem.itemId == R.id.nav_add) {
//                val intent = Intent(this, AddProduct::class.java)
//                startActivity(intent)
//                false
//            } else if (menuItem.itemId == R.id.nav_view) {
//                val intent = Intent(this, ViewProduct::class.java)
//                startActivity(intent)
//                true
//            } else if (menuItem.itemId == R.id.nav_remove) {
//                true
//            } else if (menuItem.itemId == R.id.nav_update) {
//                val intent = Intent(this, UpdateProduct::class.java)
//                startActivity(intent)
//                true
//            } else {
//                true
//            }
//        }

        var linesUnit = resources.getStringArray(R.array.dd_product_search).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddProductRemoveFilled.setAdapter(adapterDDUnit)


        //db initialise
        val passphrase: ByteArray = SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        val room =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
        val productDAO = room.productDao()

        //recycle view
        val arr = productDAO.getAll()
        val adapter = AddProduct.CustomAdapter(arr)
        AddProduct.recyclerView = binding.rvAddProduct
        AddProduct.recyclerView.setHasFixedSize(false)
        AddProduct.recyclerView.adapter = adapter
        AddProduct.recyclerView.layoutManager = LinearLayoutManager(this)



        binding.tvRemove.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.ddProductRemoveFilled.editableText.toString().toIntOrNull() == null) {
                    val arr = productDAO.getAll()
                    val adapter = AddProduct.CustomAdapter(arr)
                    AddProduct.recyclerView = binding.rvAddProduct
                    AddProduct.recyclerView.setHasFixedSize(false)
                    AddProduct.recyclerView.adapter = adapter
                    AddProduct.recyclerView.layoutManager = LinearLayoutManager(instance)

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "ID") {
                    val arr = productDAO.findByID(binding.tvRemove.editText!!.text.toString()) as List<Product>
                    val adapter = AddProduct.CustomAdapter(arr)
                    AddProduct.recyclerView = binding.rvAddProduct
                    AddProduct.recyclerView.setHasFixedSize(false)
                    AddProduct.recyclerView.adapter = adapter
                    AddProduct.recyclerView.layoutManager = LinearLayoutManager(instance)

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "Barcode") {

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "Name") {

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "Price") {

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "Category") {

                } else if(binding.ddProductRemoveFilled.editableText.toString() == "Unit") {

                }

            }
        })

    }
}
