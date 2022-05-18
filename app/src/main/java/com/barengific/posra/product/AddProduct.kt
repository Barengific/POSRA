package com.barengific.posra.product

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.*
import com.barengific.posra.databinding.AddProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.ByteArrayOutputStream


class AddProduct : AppCompatActivity() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            Companion.pos = pos
        }

        var imageString = ""
        var imageBitmap: Bitmap? = null
        private var instance: AddProduct? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun encodeImage(bm: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            imageString = Base64.encodeToString(b, Base64.DEFAULT)
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun decodeImage(bm: String): Bitmap? {
            val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imageBitmap = decodedByte
            return decodedByte
        }
    }

    private lateinit var binding: AddProductActivityBinding
    lateinit var bottomNav: BottomNavigationView

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddProductActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide();
        actionBar?.hide();

        binding.btnSave.text = Deets.btnSaveUpdateStateProduct

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_add
        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
                Deets.btnSaveUpdateStateProduct = "SAVE"
                val intent = Intent(this, AddProduct::class.java)
                startActivity(intent)
                true
            } else if (menuItem.itemId == R.id.nav_view) {
                val intent = Intent(this, ViewProduct::class.java)
                startActivity(intent)
                true
            } else {
                true
            }
        }

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

        binding.tvBarcodes.editText?.setText(Deets.bc_value)

        if(Deets.btnSaveUpdateStateProduct == "UPDATE"){
            binding.tvBarcodes.editText?.setText(Deets.upBarcode)
            binding.tvName.editText?.setText(Deets.upName)
            binding.tvStockQty.editText?.setText(Deets.upStockQty)
            binding.tvPrice.editText?.setText(Deets.upPrice)
            binding.ddCateFilled.setText(Deets.upCategory)
            binding.ddUnitFilled.setText(Deets.upUnit)
            binding.tvUnitAs.editText?.setText(Deets.upUnitAs)
            binding.imageView.setImageBitmap(Deets.upImageBitmapProduct)
        }

        if(Deets.btnSaveUpdateStateProduct == "SAVE"){
            binding.btnSave.setOnClickListener {
                val aa = Product(
                    0,
                    binding.tvBarcodes.editText?.text.toString(),
                    binding.tvName.editText?.text.toString(),
                    binding.tvStockQty.editText?.text.toString(),
                    binding.tvPrice.editText?.text.toString(),
                    binding.ddCate.editText?.text.toString(),
                    binding.ddUnit.editText?.text.toString(),
                    binding.tvUnitAs.editText?.text.toString(),
                    imageString
                )
                productDAO.insertAll(aa)

                val arrr = productDAO.getAll()
                val adapter = ProductAdapter(arrr)
                recyclerView.setHasFixedSize(false)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
                //TODO check for duplicates
            }
        }else if (Deets.btnSaveUpdateStateProduct == "UPDATE"){
            Log.d("aaaaaa", "inbupppdsate")
            binding.btnSave.setOnClickListener {
                val aa = Product(
                    Deets.upIdProduct.toInt(),
                    binding.tvBarcodes.editText?.text.toString(),
                    binding.tvName.editText?.text.toString(),
                    binding.tvStockQty.editText?.text.toString(),
                    binding.tvPrice.editText?.text.toString(),
                    binding.ddCate.editText?.text.toString(),
                    binding.ddUnit.editText?.text.toString(),
                    binding.tvUnitAs.editText?.text.toString(),
                    encodeImage(binding.imageView.drawToBitmap())
                )
//                productDAO.insertAll(aa)
                productDAO.updateProduct(aa)

                val arrr = productDAO.getAll()
                val adapter = ProductAdapter(arrr)
                recyclerView.setHasFixedSize(false)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
                //TODO check for duplicates
            }
        }

        var linesCate = resources.getStringArray(R.array.dd_cate).toList()
        var adapterDDCate = ArrayAdapter(this, R.layout.dd_layout, linesCate)
        binding.ddCateFilled.setAdapter(adapterDDCate)

        var linesUnit = resources.getStringArray(R.array.dd_unit).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddUnitFilled.setAdapter(adapterDDUnit)

        binding.tvBarcodes.setEndIconOnClickListener {
            val intent = Intent(this, CamActivity::class.java)
            startActivity(intent)
        }

        binding.imageView.setOnClickListener {
            dispatchTakePictureIntent()
        }

    }

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap
            val newBitmap: Bitmap? = getResizedBitmap(imageBitmap, 100,100)

            binding.imageView.setImageBitmap(newBitmap)
            newBitmap?.let { encodeImage(it) }
        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        val matrix = Matrix()

        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val str: String = Base64.encodeToString(b, Base64.DEFAULT)
        imageString = Base64.encodeToString(b, Base64.DEFAULT)
        binding.tvUnitAs.editText?.setText(str.length.toString())
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeImage(bm: String): Bitmap? {
        val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        imageBitmap = decodedByte
        return decodedByte
    }

}
