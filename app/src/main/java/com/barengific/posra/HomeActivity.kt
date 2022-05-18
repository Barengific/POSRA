package com.barengific.posra

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.barengific.posra.databinding.HomeActivityBinding
import com.barengific.posra.product.AddProduct
import com.barengific.posra.product.ViewProduct
import com.barengific.posra.staff.ViewStaff
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@ExperimentalPermissionsApi
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide();
        actionBar?.hide();

        binding.btnServeCustomer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("barcodeSca", message)
            startActivity(intent)
        }
        binding.btnCameraPermission.setOnClickListener {
            setupPermissions()
        }
        binding.btnProduct.setOnClickListener {
            val intent = Intent(this, ViewProduct::class.java)
//            intent.putExtra("barcodeSca", message)
            startActivity(intent)

        }
        binding.btnStaff.setOnClickListener {
            val intent = Intent(this, ViewStaff::class.java)
            startActivity(intent)
        }

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission denied")
            makeRequest()
        }
    }

    val RECORD_REQUEST_CODE: Int = 101

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG", "Permission denied")
                } else {
                    Log.i("TAG", "Permission granted")
                }
            }
        }
    }
}