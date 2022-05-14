package com.barengific.posra.product

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.barengific.posra.HomeActivity
import com.barengific.posra.R
import com.barengific.posra.databinding.RemoveProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView

class RemoveProduct : AppCompatActivity() {

    private lateinit var binding: RemoveProductActivityBinding
    lateinit var bottomNav: BottomNavigationView


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RemoveProductActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_remove
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
                val intent = Intent(this, ViewProduct::class.java)
                startActivity(intent)
                true
            } else if (menuItem.itemId == R.id.nav_remove) {
                true
            } else if (menuItem.itemId == R.id.nav_update) {
                val intent = Intent(this, UpdateProduct::class.java)
                startActivity(intent)
                true
            } else {
                true
            }
        }

        var linesUnit = resources.getStringArray(R.array.dd_product_search).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddProductRemoveFilled.setAdapter(adapterDDUnit)

    }
}
