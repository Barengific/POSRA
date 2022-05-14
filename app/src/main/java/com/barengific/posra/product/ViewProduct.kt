package com.barengific.posra.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.barengific.posra.HomeActivity
import com.barengific.posra.R
import com.barengific.posra.databinding.ViewProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewProduct : AppCompatActivity() {

    private lateinit var binding: ViewProductActivityBinding
    lateinit var bottomNav: BottomNavigationView


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ViewProductActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_view

        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
                val intent = Intent(this, AddProduct::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_view) {
                true
            } else if (menuItem.itemId == R.id.nav_remove) {
                val intent = Intent(this, RemoveProduct::class.java)
                startActivity(intent)
                true
            } else if (menuItem.itemId == R.id.nav_update) {
                val intent = Intent(this, UpdateProduct::class.java)
                startActivity(intent)
                true
            } else {
                true
            }
        }

    }
}
