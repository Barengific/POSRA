package com.barengific.posra.product

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.barengific.posra.databinding.ViewProductActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class ViewProduct : AppCompatActivity() {

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

        private var instance: ViewProduct? = null

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
                val intent = Intent(this, AddProduct::class.java)
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
        val adapter = CustomAdapter(arr)
        recyclerView = binding.rvAddProduct
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.tvSearch.setStartIconOnClickListener {
            when {
                binding.ddProductSearchFilled.text.toString().isNullOrEmpty() == null -> {
                    val arr = productDAO.getAll()
                    val adapter = CustomAdapter(arr)
                    recyclerView = binding.rvAddProduct
                    recyclerView.setHasFixedSize(false)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(instance)

                }
                binding.ddProductSearchFilled.text.toString() == "ID" -> {
                    val arr =
                        productDAO.findByID(binding.tvSearch.editText?.text.toString())
                    val adapter = CustomAdapter(arr)
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
                    val adapter = CustomAdapter(arr)
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
                    val adapter = CustomAdapter(arr)
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
                    val adapter = CustomAdapter(arr)
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
                    val adapter = CustomAdapter(arr)
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
                    val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
                        recyclerView = binding.rvAddProduct
                        recyclerView.setHasFixedSize(false)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(instance)

                    }
                    binding.ddProductSearchFilled.text.toString() == "ID" -> {
                        val arr =
                            productDAO.findByID(binding.tvSearch.editText?.text.toString())
                        val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
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
                        val adapter = CustomAdapter(arr)
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

    class CustomAdapter(private val dataSet: List<Product>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnCreateContextMenuListener {
            var ivMore: ImageView

            @SuppressLint("ResourceType")
            override fun onCreateContextMenu(menu: ContextMenu, v: View?,
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

                            val a = Product(
                                id.text.toString().toInt(),
                                barcode?.text.toString(),
                                name?.text.toString(),
                                stockQty?.text.toString(),
                                price?.text.toString(),
                                category?.text.toString(),
                                unit?.text.toString(),
                                unitAs?.text.toString(),
                                AddProduct.imageString
                            )
                            room?.productDao()?.delete(a)

                            val arr = productDao?.getAll()
                            val adapter = arr?.let { CustomAdapter(it) }
                            recyclerView.setHasFixedSize(false)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(instance)

                            ViewProduct.instance?.runOnUiThread {
                                adapter?.notifyDataSetChanged()
                            }

                            room?.close()

                        }
                        R.id.menu_update -> {
                            Deets.upIdProduct = viewHolder.tvId.text.toString()
                            Deets.upBarcode = viewHolder.tvBarcode?.text.toString()
                            Deets.upName = viewHolder.tv_name?.text.toString()
                            Deets.upStockQty = viewHolder.tv_stockQty?.text.toString()
                            Deets.upPrice = viewHolder.tv_price?.text.toString()
                            Deets.upCategory = viewHolder.tv_category?.text.toString()
                            Deets.upUnit = viewHolder.tv_unit?.text.toString()
                            Deets.upUnitAs = viewHolder.tv_unit_as?.text.toString()

                            val intent = Intent(view.context, UpdateProduct::class.java)
                            view.context.startActivity(intent)

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
            viewHolder.tvId?.text = dataSet[position]?.id?.toString()
            viewHolder.tvBarcode?.text = dataSet[position]?.barcode?.toString()
            viewHolder.tv_name?.text = dataSet[position]?.name?.toString()
            viewHolder.tv_stockQty?.text = dataSet[position]?.stockQty?.toString()
            viewHolder.tv_price?.text = dataSet[position]?.price?.toString()
            viewHolder.tv_category?.text = dataSet[position]?.category?.toString()
            viewHolder.tv_unit?.text = dataSet[position]?.unit?.toString()
            viewHolder.tv_unit_as?.text = dataSet[position]?.unit_as?.toString()

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

