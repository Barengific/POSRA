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
import com.barengific.posra.*
import com.barengific.posra.databinding.ViewProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class ProductView : AppCompatActivity() {

    private lateinit var binding: ViewProductActivityBinding
    lateinit var bottomNav: BottomNavigationView

    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            Companion.pos = pos
        }

        private var instance: ProductView? = null

        fun applicationContext(): Context? {
            return instance?.applicationContext
        }

    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ViewProductActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_view

        supportActionBar?.hide();
        actionBar?.hide();

        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
                val intent = Intent(this, ProductAdd::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_view) {
                true
            } else {
                true
            }
        }

        var linesUnit = resources.getStringArray(R.array.dd_product_search).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddProductSearchFilled.setAdapter(adapterDDUnit)

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
        val adapter = ProductAdapter(arr)
        recyclerView = binding.rvAddProduct
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.tvSearch.setStartIconOnClickListener {
            when {
                binding.ddProductSearchFilled.text.toString().isNullOrEmpty() == null -> {
                    val arr = productDAO.getAll()
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)

                }
                binding.ddProductSearchFilled.text.toString() == "ID" -> {
                    val arr =
                        productDAO.findByID(binding.tvSearch.editText?.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddProductSearchFilled.text.toString() == "Barcode" -> {
                    val arr =
                        productDAO.findByBarcode(binding.tvSearch.editText!!.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddProductSearchFilled.text.toString() == "Name" -> {
                    val arr =
                        productDAO.findByName(binding.tvSearch.editText!!.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddProductSearchFilled.text.toString() == "Price" -> {
                    val arr =
                        productDAO.findByPrice(binding.tvSearch.editText!!.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddProductSearchFilled.text.toString() == "Category" -> {
                    val arr =
                        productDAO.findByCategory(binding.tvSearch.editText!!.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddProductSearchFilled.text.toString() == "Unit" -> {
                    val arr =
                        productDAO.findByUnit(binding.tvSearch.editText!!.text.toString())
                    val adapter = ProductAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)

                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        }


        binding.tvSearch.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    binding.ddProductSearchFilled.text.toString().isNullOrEmpty() == null -> {
                        val arr = productDAO.getAll()
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)

                    }
                    binding.ddProductSearchFilled.text.toString() == "ID" -> {
                        val arr =
                            productDAO.findByID(binding.tvSearch.editText?.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddProductSearchFilled.text.toString() == "Barcode" -> {
                        val arr =
                            productDAO.findByBarcode(binding.tvSearch.editText!!.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddProductSearchFilled.text.toString() == "Name" -> {
                        val arr =
                            productDAO.findByName(binding.tvSearch.editText!!.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddProductSearchFilled.text.toString() == "Price" -> {
                        val arr =
                            productDAO.findByPrice(binding.tvSearch.editText!!.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddProductSearchFilled.text.toString() == "Category" -> {
                        val arr =
                            productDAO.findByCategory(binding.tvSearch.editText!!.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddProductSearchFilled.text.toString() == "Unit" -> {
                        val arr =
                            productDAO.findByUnit(binding.tvSearch.editText!!.text.toString())
                        val adapter = ProductAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)

                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }
}

