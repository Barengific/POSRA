package com.barengific.posra.staff

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

class StaffAdapter(private val dataSet: List<Staff>) :
    RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {
        var ivMore: ImageView
        var ivItem: ImageView

        @SuppressLint("ResourceType")
        override fun onCreateContextMenu(menu: ContextMenu, v: View?,
                                         menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            Deets.posStaff = adapterPosition
            Deets.setPosiStaff(layoutPosition)
        }

        val tv_id: TextView
        val tv_pus: TextView?
        val tv_name_staff: TextView?
        val tv_first_name: TextView?
        val tv_last_name: TextView?
        val tv_email: TextView?
        val tv_phone: TextView?
        val tv_date_hired: TextView?
        val tv_location: TextView?
        val tv_job_title: TextView?

        init {
            ivMore = view.findViewById(R.id.ivMore) as ImageView
            ivItem = view.findViewById(R.id.ivItem) as ImageView
            view.setOnCreateContextMenuListener(this)

            // Define click listener for the ViewHolder's View.
            tv_id = view.findViewById(R.id.tv_id_staff)
            tv_pus = view.findViewById(R.id.tvPus)
            tv_name_staff = view.findViewById(R.id.tv_name_staff)
            tv_first_name = view.findViewById(R.id.tv_first_name)
            tv_last_name = view.findViewById(R.id.tv_last_name)
            tv_email = view.findViewById(R.id.tv_email)
            tv_phone = view.findViewById(R.id.tv_phone)
            tv_date_hired = view.findViewById(R.id.tv_date_hired)
            tv_location = view.findViewById(R.id.tv_location)
            tv_job_title = view.findViewById(R.id.tv_job_title)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rv_staff, viewGroup, false)

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
                        val staffDao = room?.staffDao()

                        val id: TextView = viewHolder.tv_id
                        val tvPus: TextView? = viewHolder.tv_pus
                        val tv_name_staff: TextView? = viewHolder.tv_name_staff
                        val tv_first_name: TextView? = viewHolder.tv_first_name
                        val tv_last_name: TextView? = viewHolder.tv_last_name
                        val tv_email: TextView? = viewHolder.tv_email
                        val tv_phone: TextView? = viewHolder.tv_phone
                        val tv_date_hired: TextView? = viewHolder.tv_date_hired
                        val tv_location: TextView? = viewHolder.tv_location
                        val tv_job_title: TextView? = viewHolder.tv_job_title
                        val ivItem: ImageView? = viewHolder.ivItem

                        val a = Staff(
                            id.text.toString().toInt(),
                            tvPus?.text.toString(),
                            tv_first_name?.text.toString(),
                            tv_last_name?.text.toString(),
                            tv_email?.text.toString(),
                            tv_phone?.text.toString(),
                            tv_date_hired?.text.toString(),
                            tv_location?.text.toString(),
                            tv_job_title?.text.toString(),
                            Deets.encodeImage((ivItem?.drawable as BitmapDrawable).bitmap)
                        )
                        room?.staffDao()?.delete(a)

                        val arrr = staffDao?.getAll()
                        val adapter = arrr?.let { StaffAdapter(it) }
                        Deets.recyclerView.setHasFixedSize(false)
                        Deets.recyclerView.adapter = adapter
                        Deets.recyclerView.layoutManager =
                            LinearLayoutManager(view?.context)

                        adapter?.notifyDataSetChanged()

                        room?.close()
//                        Log.d("aaa menu", "DDDelete")

                    }
                    R.id.menu_update -> {
                        Deets.btnSaveUpdateStateStaff = "UPDATE"

                        Deets.upIdStaff = viewHolder.tv_id.text.toString()
                        Deets.upPus = viewHolder.tv_pus?.text.toString()
                        Deets.upFirst = viewHolder.tv_first_name?.text.toString()
                        Deets.upLast = viewHolder.tv_last_name?.text.toString()
                        Deets.upEmail = viewHolder.tv_email?.text.toString()
                        Deets.upPhone = viewHolder.tv_phone?.text.toString()
                        Deets.upDateHired = viewHolder.tv_date_hired?.text.toString()
                        Deets.upLocation = viewHolder.tv_location?.text.toString()
                        Deets.upJobTitle = viewHolder.tv_job_title?.text.toString()
                        Deets.upImageBitmapStaff = viewHolder.ivItem.drawToBitmap()

                        val intent = Intent(view.context, StaffAdd::class.java)
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

        viewHolder.tv_id.text = dataSet[position].id.toString()
        viewHolder.tv_pus?.text = dataSet[position].password.toString()
        viewHolder.tv_name_staff?.text = dataSet[position].firstName + " " + dataSet[position].lastName
        viewHolder.tv_first_name?.text = dataSet[position].firstName.toString()
        viewHolder.tv_last_name?.text = dataSet[position].lastName.toString()
        viewHolder.tv_email?.text = dataSet[position].email.toString()
        viewHolder.tv_phone?.text = dataSet[position].phoneNumber.toString()
        viewHolder.tv_date_hired?.text = dataSet[position].dateHired.toString()
        viewHolder.tv_location?.text = dataSet[position].location.toString()
        viewHolder.tv_job_title?.text = dataSet[position].jobTitle.toString()
        viewHolder.ivItem.setImageBitmap(dataSet[position].image?.let { StaffAdd.decodeImage(it) })

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