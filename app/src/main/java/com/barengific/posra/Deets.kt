package com.barengific.posra

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.barengific.posra.product.AddProduct
import com.barengific.posra.product.Product
import com.barengific.posra.staff.Staff
import java.io.ByteArrayOutputStream

class Deets {
    companion object{
        var arrBasket: MutableList<Basket> = mutableListOf(Basket(0, "", "", "", ""))
        var arrProduct: MutableList<Product> = mutableListOf(Product(0, "", "", "", "","","","",""))
        var arrStaff: MutableList<Staff> = mutableListOf(Staff(0, "", "", "", "","","","",""))
        var bc_value = ""

        var posProduct: Int = 0
        fun setPosiProduct(pos: Int) {
            posProduct = pos
        }

        //update product row
        var upIdProduct = ""
        var upBarcode = ""
        var upName = ""
        var upStockQty = ""
        var upPrice = ""
        var upCategory = ""
        var upUnit = ""
        var upUnitAs = ""

        //update staff row
        var upIdStaff = ""
        var upPus = ""
        var upFirst = ""
        var upLast = ""
        var upEmail = ""
        var upPhone = ""
        var upDateHired = ""
        var upLocation = ""
        var upJobTitle = ""

        lateinit var recyclerView: RecyclerView

        fun encodeImage(bm: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            AddProduct.imageString = Base64.encodeToString(b, Base64.DEFAULT)
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun decodeImage(bm: String): Bitmap? {
            val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            AddProduct.imageBitmap = decodedByte
            return decodedByte
        }
    }

}