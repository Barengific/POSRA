package com.barengific.posra.product

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.AppDatabase
import com.barengific.posra.Deets
import com.barengific.posra.R
import net.sqlcipher.database.SupportFactory


class ProductAdapter(private val dataSet: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {
        var ivMore: ImageView
        var ivItem: ImageView

        @SuppressLint("ResourceType")
        override fun onCreateContextMenu(menu: ContextMenu, v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            AddProduct.pos = adapterPosition
            AddProduct.setPosi(layoutPosition)
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
                            AddProduct.encodeImage((ivItem?.drawable as BitmapDrawable).bitmap)
                        )
                        room?.productDao()?.delete(a)

                        val arrr = Deets.arrProduct

                        val adapter = ProductAdapter(arrr)

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
        viewHolder.tvBarcode?.text = dataSet[position].barcode.toString()
        viewHolder.tv_name?.text = dataSet[position].name.toString()
        viewHolder.tv_stockQty?.text = dataSet[position].stockQty.toString()
        viewHolder.tv_price?.text = dataSet[position].price.toString()
        viewHolder.tv_category?.text = dataSet[position].category.toString()
        viewHolder.tv_unit?.text = dataSet[position].unit.toString()
        viewHolder.tv_unit_as?.text = dataSet[position].unit_as.toString()
        viewHolder.ivItem.setImageBitmap(dataSet[position].image?.let { AddProduct.decodeImage(it) })

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