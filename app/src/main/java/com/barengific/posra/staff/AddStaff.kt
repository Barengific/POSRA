package com.barengific.posra.staff

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
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.*
import com.barengific.posra.databinding.AddStaffActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.ByteArrayOutputStream

class AddStaff : AppCompatActivity() {
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
        private var instance: AddStaff? = null

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

    private lateinit var binding: AddStaffActivityBinding
    lateinit var bottomNav: BottomNavigationView

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddStaffActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide();
        actionBar?.hide();

        binding.btnSave.text = Deets.btnSaveUpdateStateStaff

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_add
        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
                Deets.btnSaveUpdateStateStaff = "SAVE"
                val intent = Intent(this, AddStaff::class.java)
                startActivity(intent)
                true
            } else if (menuItem.itemId == R.id.nav_view) {
                val intent = Intent(this, ViewStaff::class.java)
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
        val staffDao = room.staffDao()

        //recycle view
        val arr = staffDao.getAll()
        val adapter = StaffAdapter(arr)
        recyclerView = binding.rvAddStaff
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

//        binding.tvBarcodes.editText?.setText(Deets.bc_value)

        if(Deets.btnSaveUpdateStateStaff == "UPDATE"){
            binding.tvPus.editText?.setText(Deets.upPus)
            binding.tvFirstName.editText?.setText(Deets.upFirst)
            binding.tvLastName.editText?.setText(Deets.upLast)
            binding.tvEmail.editText?.setText(Deets.upEmail)
            binding.tvPhone.editText?.setText(Deets.upPhone)
            binding.ddJobTitle.editText?.setText(Deets.upDateHired)
            binding.ddLocation.editText?.setText(Deets.upLocation)
            //TODO set date
            binding.tdDateHired.dayOfMonth.toString() + binding.tdDateHired.month.toString() + binding.tdDateHired.year.toString()
            binding.imageView.setImageBitmap(Deets.upImageBitmapStaff)
        }

        if(Deets.btnSaveUpdateStateStaff == "SAVE"){
            binding.btnSave.setOnClickListener {
                val aa = Staff(
                    0,
                    binding.tvPus.editText?.text.toString(),
                    binding.tvFirstName.editText?.text.toString(),
                    binding.tvLastName.editText?.text.toString(),
                    binding.tvEmail.editText?.text.toString(),
                    binding.tvPhone.editText?.text.toString(),
                    binding.ddJobTitle.editText?.text.toString(),
                    binding.ddLocation.editText?.text.toString(),
                    binding.tdDateHired.dayOfMonth.toString() + binding.tdDateHired.month.toString() + binding.tdDateHired.year.toString(),
                    imageString
                )
                staffDao.insertAll(aa)

                val arrr = staffDao.getAll()
                val adapter = StaffAdapter(arrr)
                recyclerView.setHasFixedSize(false)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
                //TODO check for duplicates
            }
        }else if (Deets.btnSaveUpdateStateStaff == "UPDATE"){
            binding.btnSave.setOnClickListener {
                val aa = Staff(
                    Deets.upIdStaff.toInt(),
                    binding.tvPus.editText?.text.toString(),
                    binding.tvFirstName.editText?.text.toString(),
                    binding.tvLastName.editText?.text.toString(),
                    binding.tvEmail.editText?.text.toString(),
                    binding.tvPhone.editText?.text.toString(),
                    binding.ddJobTitle.editText?.text.toString(),
                    binding.ddLocation.editText?.text.toString(),
                    binding.tdDateHired.dayOfMonth.toString() + binding.tdDateHired.month.toString() + binding.tdDateHired.year.toString(),
                    encodeImage(binding.imageView.drawToBitmap())
                )
                staffDao.updateStaff(aa)

                val arrr = staffDao.getAll()
                val adapter = StaffAdapter(arrr)
                recyclerView.setHasFixedSize(false)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
                //TODO check for duplicates
            }
        }

        var linesCate = resources.getStringArray(R.array.dd_staff_location).toList()
        var adapterDDCate = ArrayAdapter(this, R.layout.dd_layout, linesCate)
        binding.ddLocationFilled.setAdapter(adapterDDCate)

        var linesUnit = resources.getStringArray(R.array.dd_staff_job_title).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddJobFilled.setAdapter(adapterDDUnit)


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
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeImage(bm: String): Bitmap? {
        val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        imageBitmap = decodedByte
        return decodedByte
    }

}
