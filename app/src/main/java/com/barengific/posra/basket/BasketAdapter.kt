package com.barengific.posra.basket

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.AppDatabase
import com.barengific.posra.Deets
import com.barengific.posra.MainActivity
import com.barengific.posra.R
import net.sqlcipher.database.SupportFactory

class BasketAdapter(private val dataSet: MutableList<Basket>) :
    RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

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
        val tv_name: TextView
        val tv_price: TextView
        val tv_qty: TextView
        val tv_total: TextView
        val tv_barcode_basket: TextView

        init {
            ivMore = view.findViewById(R.id.ivMore) as ImageView
            view.setOnCreateContextMenuListener(this)

            // Define click listener for the ViewHolder's View.
            tv_id = view.findViewById(R.id.tv_id)
            tv_name = view.findViewById(R.id.tv_name)
            tv_price = view.findViewById(R.id.tv_price)
            tv_qty = view.findViewById(R.id.tv_qty)
            tv_total = view.findViewById(R.id.tv_total)
            tv_barcode_basket = view.findViewById(R.id.tv_barcode_basket)
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
            popup.inflate(R.menu.rv_context_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        val id: TextView = viewHolder.tv_id
                        val name: TextView = viewHolder.tv_name
                        val price: TextView = viewHolder.tv_price
                        val qty: TextView = viewHolder.tv_qty
                        val total: TextView = viewHolder.tv_total
                        val barcode: TextView = viewHolder.tv_barcode_basket

                        val priceStr = price.text.drop(3)
                        val qtyStr = qty.text.dropLast(1)
                        val totalStr = total.text.drop(3)

                        val a = Basket(
                            id.text.toString().toInt(),
                            name.text.toString(),
                            priceStr.toString(), // remove d.
                            qtyStr.toString(), // remove x
                            totalStr.toString(), // remove d.
                            barcode.text.toString(),
                        )

                        Deets.arrBasket?.remove(a)
                        val delIndex = Deets.arrBasket.indexOf(a)
                        Log.d("aaaaaDELINDEX", delIndex.toString())
//                        Deets.arrBasket.removeAt(delIndex)

                        val arrr = Deets.arrBasket
                        Log.d("aaaaa", Deets.arrBasket.toString())

                        val adapter = arrr?.let { BasketAdapter(it) }

                        MainActivity.recyclerView.setHasFixedSize(false)
                        MainActivity.recyclerView.adapter = adapter
                        MainActivity.recyclerView.layoutManager =
                            LinearLayoutManager(view?.context)
//                        Log.d("aaa menu", "DDDelete")

                        adapter?.notifyDataSetChanged()

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
        viewHolder.tv_price.text = "d. " + dataSet[position].price.toString()
        viewHolder.tv_qty.text = dataSet[position].qty.toString() + "x"
        viewHolder.tv_total.text = "d. " + dataSet[position].total.toString()
        viewHolder.tv_barcode_basket.text = "d. " + dataSet[position].barcode.toString()
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