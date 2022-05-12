package com.barengific.posra

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
import com.barengific.posra.databinding.AddproductLayoutBinding
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class AddProduct : AppCompatActivity() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            this.pos = pos
        }

        private var instance: AddProduct? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    private lateinit var binding: AddproductLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddproductLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db initialise
        val passphrase: ByteArray = SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        val room = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")
            .openHelperFactory(factory)
            .allowMainThreadQueries()
            .build()
        val productDAO = room.productDao()

        //recycle view
        val arr = productDAO.getAll()
        val adapter = CustomAdapter(arr)
        recyclerView = findViewById<View>(R.id.rView) as RecyclerView
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnSave.setOnClickListener {
            val aa = Product(
                0,
                binding.tvBarcode.editText?.text.toString(),
                binding.tvName.editText?.text.toString(),
                binding.tvDesc.editText?.text.toString(),
                binding.tvStockQty.editText?.text.toString(),
                binding.tvPrice.editText?.text.toString(),
                binding.ddCate.editText?.text.toString(),
                binding.ddUnit.editText?.text.toString(),
                binding.tvUnitAs.editText?.text.toString()
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


        binding.tvDone.editText?.setText("changeMe")
        binding.tvBarcode.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvDone.editText?.setText(binding.tvBarcode.editText?.text.toString())
            }
            })

    }

    class CustomAdapter(private val dataSet: List<Product>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnCreateContextMenuListener {
            var ivMore: ImageView

            @SuppressLint("ResourceType")
            override fun onCreateContextMenu(
                menu: ContextMenu,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo?
            ) {
                AddProduct.pos = adapterPosition
                AddProduct.setPosi(layoutPosition)
            }

            val tvId: TextView
            val tvBarcode: TextView
            val tv_name: TextView
            val tv_description: TextView
            val tv_stockQty: TextView
            val tv_price: TextView
            val tv_category: TextView
            val tv_unit: TextView
            val tv_unit_as: TextView

            init {
                ivMore = view.findViewById(R.id.ivMore) as ImageView
                view.setOnCreateContextMenuListener(this)

                // Define click listener for the ViewHolder's View.
                tvId = view.findViewById(R.id.tv_id)
                tvBarcode = view.findViewById(R.id.tv_barcode)
                tv_name = view.findViewById(R.id.tv_name)
                tv_description = view.findViewById(R.id.tv_description)
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
                .inflate(R.layout.rv_basket, viewGroup, false)

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
                popup.inflate(R.menu.rv_basket_context_menu)
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
                            val barcode: TextView = viewHolder.tvBarcode
                            val name: TextView = viewHolder.tv_name
                            val description: TextView = viewHolder.tv_description
                            val stockQty: TextView = viewHolder.tv_stockQty
                            val price: TextView = viewHolder.tv_price
                            val category: TextView = viewHolder.tv_category
                            val unit: TextView = viewHolder.tv_unit
                            val unit_as: TextView = viewHolder.tv_unit_as

                            val a = Product(
                                id.text.toString().toInt(),
                                barcode.text.toString(),
                                name.text.toString(),
                                description.text.toString(),
                                stockQty.text.toString(),
                                price.text.toString(),
                                category.text.toString(),
                                unit.text.toString(),
                                unit_as.text.toString()
                            )
                            room?.productDao()?.delete(a)
                            val arrr = Deets.arrr

                            val adapter = arrr?.let { CustomAdapter(it) }

                            AddProduct.recyclerView.setHasFixedSize(false)
                            AddProduct.recyclerView.adapter = adapter
                            AddProduct.recyclerView.layoutManager =
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
            viewHolder.tvBarcode.text = dataSet[position].barcode.toString()
            viewHolder.tv_name.text = dataSet[position].name.toString()
            viewHolder.tv_description.text = dataSet[position].description.toString()
            viewHolder.tv_stockQty.text = dataSet[position].stockQty.toString()
            viewHolder.tv_price.text = dataSet[position].price.toString()
            viewHolder.tv_category.text = dataSet[position].category.toString()
            viewHolder.tv_unit.text = dataSet[position].unit.toString()
            viewHolder.tv_unit_as.text = dataSet[position].unit_as.toString()

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