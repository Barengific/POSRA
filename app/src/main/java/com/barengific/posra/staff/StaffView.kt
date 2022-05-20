package com.barengific.posra.staff

import android.content.Context
import android.content.Intent
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
import com.barengific.posra.R
import com.barengific.posra.databinding.ViewStaffActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class StaffView : AppCompatActivity() {
    private lateinit var binding: ViewStaffActivityBinding
    lateinit var bottomNav: BottomNavigationView

    companion object {
        var pos: Int = 0
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            Companion.pos = pos
        }

        var instance: StaffView? = null

        fun applicationContext(): Context? {
            return instance?.applicationContext
        }

    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ViewStaffActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide();
        actionBar?.hide();

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_view

        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
                val intent = Intent(this, StaffAdd::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_view) {
                true
            } else {
                true
            }
        }

        var linesUnit = resources.getStringArray(R.array.dd_staff_search).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddStaffSearchFilled.setAdapter(adapterDDUnit)

        //db initialise
        val passphrase: ByteArray = SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        val room =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
        val staffDao = room.staffDao()

        //recycle view
        val arr = staffDao.getAll()
        val adapter = StaffAdapter(arr)
        StaffView.recyclerView = binding.rvAddStaff
        StaffView.recyclerView.setHasFixedSize(false)
        StaffView.recyclerView.adapter = adapter
        StaffView.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.tvSearch.setStartIconOnClickListener {
            when {
                binding.ddStaffSearchFilled.text.toString().isNullOrEmpty() == null -> {
                    val arr = staffDao.getAll()
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)

                }
                binding.ddStaffSearchFilled.text.toString() == "ID" -> {
                    val arr =
                        staffDao.findById(binding.tvSearch.editText?.text.toString().toInt())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddStaffSearchFilled.text.toString() == "Barcode" -> {
                    val arr =
                        staffDao.findByFirstName(binding.tvSearch.editText!!.text.toString())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddStaffSearchFilled.text.toString() == "Name" -> {
                    val arr =
                        staffDao.findByLastName(binding.tvSearch.editText!!.text.toString())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddStaffSearchFilled.text.toString() == "Price" -> {
                    val arr =
                        staffDao.findByEmail(binding.tvSearch.editText!!.text.toString())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddStaffSearchFilled.text.toString() == "Category" -> {
                    val arr =
                        staffDao.findByLocation(binding.tvSearch.editText!!.text.toString())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
                binding.ddStaffSearchFilled.text.toString() == "Unit" -> {
                    val arr =
                        staffDao.findByJob(binding.tvSearch.editText!!.text.toString())
                    val adapter = StaffAdapter(arr)
                    StaffView.recyclerView = binding.rvAddStaff
                    StaffView.recyclerView.setHasFixedSize(false)
                    StaffView.recyclerView.adapter = adapter
                    StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)

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
                    binding.ddStaffSearchFilled.text.toString().isNullOrEmpty() == null -> {
                        val arr = staffDao.getAll()
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)

                    }
                    binding.ddStaffSearchFilled.text.toString() == "ID" -> {
                        val arr =
                            staffDao.findById(binding.tvSearch.editText?.text.toString().toInt())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddStaffSearchFilled.text.toString() == "Barcode" -> {
                        val arr =
                            staffDao.findByFirstName(binding.tvSearch.editText!!.text.toString())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddStaffSearchFilled.text.toString() == "Name" -> {
                        val arr =
                            staffDao.findByLastName(binding.tvSearch.editText!!.text.toString())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddStaffSearchFilled.text.toString() == "Price" -> {
                        val arr =
                            staffDao.findByEmail(binding.tvSearch.editText!!.text.toString())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddStaffSearchFilled.text.toString() == "Category" -> {
                        val arr =
                            staffDao.findByLocation(binding.tvSearch.editText!!.text.toString())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }

                    }
                    binding.ddStaffSearchFilled.text.toString() == "Unit" -> {
                        val arr =
                            staffDao.findByJob(binding.tvSearch.editText!!.text.toString())
                        val adapter = StaffAdapter(arr)
                        StaffView.recyclerView = binding.rvAddStaff
                        StaffView.recyclerView.setHasFixedSize(false)
                        StaffView.recyclerView.adapter = adapter
                        StaffView.recyclerView.layoutManager = LinearLayoutManager(instance)

                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }
}
