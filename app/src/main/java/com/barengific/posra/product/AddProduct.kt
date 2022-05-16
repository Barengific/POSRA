package com.barengific.posra.product

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_add
        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_add) {
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
        val adapter = CustomAdapter(arr)
        recyclerView = binding.rvAddProduct
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.tvBarcode.editText?.setText(Deets.bc_value)

        binding.btnSave.setOnClickListener {
            val aa = Product(
                0,
                binding.tvBarcode.editText?.text.toString(),
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
            val adapter = CustomAdapter(arrr)
            recyclerView.setHasFixedSize(false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
            //TODO check for duplicates
        }

        var linesCate = resources.getStringArray(R.array.dd_cate).toList()
        var adapterDDCate = ArrayAdapter(this, R.layout.dd_layout, linesCate)
        binding.ddCateFilled.setAdapter(adapterDDCate)

        var linesUnit = resources.getStringArray(R.array.dd_unit).toList()
        var adapterDDUnit = ArrayAdapter(this, R.layout.dd_layout, linesUnit)
        binding.ddUnitFilled.setAdapter(adapterDDUnit)

        binding.tvBarcode.setEndIconOnClickListener {
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
//        takePictureIntent.type = "image/*";
//        takePictureIntent.putExtra("crop", "true");
//        takePictureIntent.putExtra("outputX", 150);
//        takePictureIntent.putExtra("outputY", 150);
//        takePictureIntent.putExtra("aspectX", 1);
//        takePictureIntent.putExtra("aspectY", 1);
//        takePictureIntent.putExtra("scale", true);
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
            // display error state to the user
            val newBitmap: Bitmap? = getResizedBitmap(imageBitmap, 100,100)
//            binding.imageView.setImageBitmap(newBitmap)
//            imgg(newBitmap)
            binding.imageView.setImageBitmap(newBitmap)
            newBitmap?.let { encodeImage(it) }
        }
    }

    fun imgg(aa: Bitmap?){

    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
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

        binding.tvUnitAs.editText?.setText(str.length.toString())
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeImage(bm: String): Bitmap? {
        val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        return decodedByte
    }


    class CustomAdapter(private val dataSet: List<Product>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnCreateContextMenuListener {
            var ivMore: ImageView
            var ivItem: ImageView

            @SuppressLint("ResourceType")
            override fun onCreateContextMenu(
                menu: ContextMenu,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo?
            ) {
                pos = adapterPosition
                setPosi(layoutPosition)
            }

            val tvId: TextView
            val tvBarcode: TextView?
            val tv_name: TextView?
            val tv_stockQty: TextView?
            val tv_price: TextView?
            val tv_category: TextView?
            val tv_unit: TextView?
            val tv_unit_as: TextView?

            init {
                ivMore = view.findViewById(R.id.ivMore) as ImageView
                ivItem = view.findViewById(R.id.ivItem) as ImageView
                view.setOnCreateContextMenuListener(this)

                // Define click listener for the ViewHolder's View.
                tvId = view.findViewById(R.id.tv_Id)
                tvBarcode = view.findViewById(R.id.tv_barcode)
                tv_name = view.findViewById(R.id.tv_name)
                tv_stockQty = view.findViewById(R.id.tv_stockQty)
                tv_price = view.findViewById(R.id.tv_price)
                tv_category = view.findViewById(R.id.tv_category)
                tv_unit = view.findViewById(R.id.tv_unit)
                tv_unit_as = view.findViewById(R.id.tv_unit_as)
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.rv_product, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            viewHolder: ViewHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            viewHolder.itemView.setOnLongClickListener {
                setPosition(viewHolder.layoutPosition)
                setPosition(viewHolder.adapterPosition)
                false
            }

            viewHolder.ivMore.setOnClickListener { view ->
                val wrapper: Context = ContextThemeWrapper(view?.context, R.style.PopupMenu)

                val popup = PopupMenu(wrapper, viewHolder.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.rv_context_menu)
                //adding click listener
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_delete -> {
                            val passphrase: ByteArray =
                                net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
                            val factory = SupportFactory(passphrase)
                            val room = view?.context?.let {
                                Room.databaseBuilder(it, AppDatabase::class.java, "database-names")
                                    .openHelperFactory(factory)
                                    .allowMainThreadQueries()
                                    .build()
                            }
                            val productDao = room?.productDao()

                            val id: TextView = viewHolder.tvId
                            val barcode: TextView? = viewHolder.tvBarcode
                            val name: TextView? = viewHolder.tv_name
                            val stockQty: TextView? = viewHolder.tv_stockQty
                            val price: TextView? = viewHolder.tv_price
                            val category: TextView? = viewHolder.tv_category
                            val unit: TextView? = viewHolder.tv_unit
                            val unitAs: TextView? = viewHolder.tv_unit_as
                            val ivItem: ImageView? = viewHolder.ivItem

                            val a = Product(
                                id.text.toString().toInt(),
                                barcode?.text.toString(),
                                name?.text.toString(),
                                stockQty?.text.toString(),
                                price?.text.toString(),
                                category?.text.toString(),
                                unit?.text.toString(),
                                unitAs?.text.toString(),
                                encodeImage((ivItem?.drawable as BitmapDrawable).bitmap)
                            )
                            room?.productDao()?.delete(a)
                            val arrr = Deets.arrr

                            val adapter = arrr?.let { CustomAdapter(it) }

                            recyclerView.setHasFixedSize(false)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager =
                                LinearLayoutManager(view?.context)
                            room?.close()
//                        Log.d("aaa menu", "DDDelete")

                        }

                        R.id.menu_cancel -> {
//                        Log.d("aaa menu", "cancel")
                        }

                    }
                    true
                }
                //displaying the popup
                popup.show()
            }

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.tvId.text = dataSet[position].id.toString()
            viewHolder.tvBarcode?.text = dataSet[position].barcode.toString()
            viewHolder.tv_name?.text = dataSet[position].name.toString()
            viewHolder.tv_stockQty?.text = dataSet[position].stockQty.toString()
            viewHolder.tv_price?.text = dataSet[position].price.toString()
            viewHolder.tv_category?.text = dataSet[position].category.toString()
            viewHolder.tv_unit?.text = dataSet[position].unit.toString()
            viewHolder.tv_unit_as?.text = dataSet[position].unit_as.toString()
            viewHolder.ivItem.setImageBitmap(dataSet[position].image?.let { decodeImage(it) })

        }

        override fun onViewRecycled(holder: ViewHolder) {
            holder.itemView.setOnLongClickListener(null)
            super.onViewRecycled(holder)
        }

        override fun getItemCount() = dataSet.size

        private var position: Int = 0

//    fun getPosition(): Int {
//        return position
//    }

        private fun setPosition(position: Int) {
            this.position = position
        }

    }
}