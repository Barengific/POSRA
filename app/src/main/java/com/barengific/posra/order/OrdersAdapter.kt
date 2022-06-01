package com.barengific.posra.order

import com.barengific.posra.product.Product
import com.barengific.posra.product.ProductAdd
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.AppDatabase
import com.barengific.posra.Deets
import com.barengific.posra.R
import net.sqlcipher.database.SupportFactory

class OrdersAdapter(private val dataSet: List<Orders>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {
        var ivMore: ImageView
        var ivItem: ImageView

        @SuppressLint("ResourceType")
        override fun onCreateContextMenu(menu: ContextMenu, v: View?,
                                         menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            Deets.posProduct = adapterPosition //TODO
            Deets.setPosiProduct(layoutPosition)
        }

        val tvId: TextView
        val tvBarcode: TextView?
        val tv_name: TextView?
        val tv_stockQty: TextView?
        val tv_price: TextView?
        val tv_category: TextView?

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

        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rv_product, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder,
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
                        val ordersDao = room?.ordersDao()

                        val id: TextView = viewHolder.tvId
                        val barcode: TextView? = viewHolder.tvBarcode
                        val name: TextView? = viewHolder.tv_name
                        val stockQty: TextView? = viewHolder.tv_stockQty
                        val price: TextView? = viewHolder.tv_price
                        val category: TextView? = viewHolder.tv_category
                        val ivItem: ImageView? = viewHolder.ivItem

                        val a = Orders(
                            id.text.toString().toInt(),
                            barcode?.text.toString(),
                            name?.text.toString().toDouble(),
                            stockQty?.text.toString().toDouble(),
                            price?.text.toString().toInt(),
                            Deets.arrBasket,
                        )
                        room?.ordersDao()?.delete(a)

                        val arrr = ordersDao?.getAll()
                        val adapter = arrr?.let { OrdersAdapter(it) }
                        Deets.recyclerView.setHasFixedSize(false)
                        Deets.recyclerView.adapter = adapter
                        Deets.recyclerView.layoutManager =
                            LinearLayoutManager(view?.context)

                        adapter?.notifyDataSetChanged()

                        room?.close()
//                        Log.d("aaa menu", "DDDelete")

                    }
                    R.id.menu_update -> {
                        Deets.btnSaveUpdateStateProduct = "UPDATE"

                        Deets.upIdProduct = viewHolder.tvId.text.toString()
                        Deets.upBarcode = viewHolder.tvBarcode?.text.toString()
                        Deets.upName = viewHolder.tv_name?.text.toString()
                        Deets.upStockQty = viewHolder.tv_stockQty?.text.toString()
                        Deets.upPrice = viewHolder.tv_price?.text.toString()
                        Deets.upCategory = viewHolder.tv_category?.text.toString()
                        Deets.upImageBitmapProduct = viewHolder.ivItem.drawToBitmap()

                        val intent = Intent(view.context, ProductAdd::class.java)
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
        viewHolder.tvId.text = dataSet[position].id.toString()
        viewHolder.tvBarcode?.text = dataSet[position].date.toString()
        viewHolder.tv_name?.text = dataSet[position].totalPrice.toString()
        viewHolder.tv_stockQty?.text = dataSet[position].totalDiscount.toString()
        viewHolder.tv_price?.text = dataSet[position].totalQty.toString()
        viewHolder.tv_category?.text = dataSet[position].itemList.toString()

    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = dataSet.size

    private var position: Int = 0

    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

}