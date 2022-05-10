package com.barengific.posra

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.databinding.AddproductLayoutBinding
import com.barengific.posra.databinding.PasserActivityBinding
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class PasserActivity : AppCompatActivity() {
    private lateinit var binding: PasserActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PasserActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle? = intent.extras
        var message = bundle!!.getString("barcodeScanned") // 1
//        var strUser: String? = intent.getStringExtra("value") // 2
//        Toast.makeText(this, "In passers: $message", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, PasserActivity::class.java)
        intent.putExtra("barcodeSca", message)
        startActivity(intent)
    }
}
