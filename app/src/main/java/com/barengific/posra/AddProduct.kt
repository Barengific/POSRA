package com.barengific.posra

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.MainActivity.Companion.recyclerView
import com.barengific.posra.databinding.AddproductLayoutBinding
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class AddProduct : AppCompatActivity() {
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


    }

    private fun manageUsers(){

    }
    private fun serverCustomer(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

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
                MainActivity.pos = adapterPosition
                MainActivity.setPosi(layoutPosition)
            }

            val tv_id: TextView
            val tv_barcode: TextView
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
                tv_id = view.findViewById(R.id.tv_id)
                tv_barcode = view.findViewById(R.id.tv_barcode)
                tv_name = view.findViewById(R.id.tv_name)
                tv_description = view.findViewById(R.id.tv_description)
                tv_stockQty = view.findViewById(R.id.tv_stockQty)
                tv_price = view.findViewById(R.id.tv_price)
                tv_category = view.findViewById(R.id.tv_category)
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

                            val id: TextView = viewHolder.tv_id
                            val name: TextView = viewHolder.tv_name
                            val price: TextView = viewHolder.tv_price
                            val qty: TextView = viewHolder.tv_qty
                            val total: TextView = viewHolder.tv_total

                            val a = Basket(
                                id.text.toString().toInt(),
                                name.text.toString(),
                                price.text.toString(),
                                qty.text.toString(),
                                total.text.toString()
                            )
                            Deets.arrr.remove(a)
                            val arrr = Deets.arrr

                            val adapter = arrr?.let { CustomAdapter(it) }

                            MainActivity.recyclerView.setHasFixedSize(false)
                            MainActivity.recyclerView.adapter = adapter
                            MainActivity.recyclerView.layoutManager =
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
            viewHolder.tv_id.text = dataSet[position].id.toString()
            viewHolder.tv_name.text = dataSet[position].name.toString()
            viewHolder.tv_price.text = dataSet[position].price.toString()
            viewHolder.tv_qty.text = dataSet[position].qty.toString()
            viewHolder.tv_total.text = dataSet[position].total.toString()
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