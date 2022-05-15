package com.barengific.posra.staff

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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

class ViewStaff {
    companion object{
        fun setPosi(layoutPosition: Int) {

        }

        var pos: Int = 0
    }
}

class CustomAdapter(private val dataSet: List<Staff>) :
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
            ViewStaff.pos = adapterPosition
            ViewStaff.setPosi(layoutPosition)
        }

        val tvId: TextView
        val tvName: TextView?
        val tvEmail: TextView?
        val tvPhone: TextView?
        val tvDateHired: TextView?
        val tvLocation: TextView?
        val tvJobTitle: TextView?

        init {
            ivMore = view.findViewById(R.id.ivMore) as ImageView
            view.setOnCreateContextMenuListener(this)

            // Define click listener for the ViewHolder's View.
            tvId = view.findViewById(R.id.tv_id_staff)
            tvName = view.findViewById(R.id.tv_full_name)
            tvEmail = view.findViewById(R.id.tv_email)
            tvPhone = view.findViewById(R.id.tv_phone)
            tvDateHired = view.findViewById(R.id.tv_date_hired)
            tvLocation = view.findViewById(R.id.tv_location)
            tvJobTitle = view.findViewById(R.id.tv_job_title)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rv_staff, viewGroup, false)

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
                        val fullname: TextView? = viewHolder.tvName
                        val email: TextView? = viewHolder.tvEmail
                        val phone: TextView? = viewHolder.tvPhone
                        val dateHired: TextView? = viewHolder.tvDateHired
                        val location: TextView? = viewHolder.tvLocation
                        val jobTitle: TextView? = viewHolder.tvJobTitle

                        val a = Staff(
                            id.text.toString().toInt(),
                            fullname?.text.toString(),
                            email?.text.toString(),
                            phone?.text.toString(),
                            dateHired?.text.toString(),
                            location?.text.toString(),
                            jobTitle?.text.toString(),
                            unitAs?.text.toString()
                        )
                        room?.productDao()?.delete(a)

                        val arr = productDao?.getAll()
                        val adapter = arr?.let { CustomAdapter(it) }
                        ViewStaff.recyclerView.setHasFixedSize(false)
                        ViewStaff.recyclerView.adapter = adapter
                        ViewStaff.recyclerView.layoutManager = LinearLayoutManager(ViewStaff.instance)

                        ViewStaff.instance?.runOnUiThread {
                            adapter?.notifyDataSetChanged()
                        }

                        room?.close()

                    }
                    R.id.menu_update -> {
                        Deets.upId = viewHolder.tvId.text.toString()
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
        viewHolder.tvName?.text = dataSet[position]?.firstName?.toString()
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