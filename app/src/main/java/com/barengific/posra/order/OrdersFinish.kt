package com.barengific.posra.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.barengific.posra.R
import com.barengific.posra.databinding.OrderFinishLayoutBinding

class OrdersFinish : AppCompatActivity() {
    //TODO
    //view finished order
    //continue button to start new customer order
    //order receipt must contain:
    //store/shop/supermarket details
    //order/receipt number
    //date & time of purchase
    //list of items, items qty, item price, total of item
    //total order cost
    //total order qty
    //terms & conditions of store/shop
    //

    private lateinit var binding: OrderFinishLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_finish_layout)

        binding = OrderFinishLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}