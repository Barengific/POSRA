package com.barengific.posra

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.barengific.posra.basket.Basket
import com.barengific.posra.product.ProductAdd
import com.barengific.posra.product.Product
import com.barengific.posra.staff.Staff
import java.io.ByteArrayOutputStream

class Deets {
    companion object{
        var arrBasket: MutableList<Basket> = mutableListOf(Basket(0, "0", "", "", "",""))
        var totalPriceBasket = 0.0;
        var totalQtyBasket = 0;
//        var arrBasket: MutableList<Basket> = mutableListOf(Basket(0, "Name", "Price", "Qty", "Total","Barcode"))
        var arrProduct: MutableList<Product> = mutableListOf(Product(0, "", "", "", "","","","",""))
        var arrStaff: MutableList<Staff> = mutableListOf(Staff(0, "", "", "", "","","","","", ""))
        var bc_value = ""

        var btnSaveUpdateStateProduct = "SAVE"
        var btnSaveUpdateStateStaff = "SAVE"

        var posProduct: Int = 0
        fun setPosiProduct(pos: Int) {
            posProduct = pos
        }

        var posStaff: Int = 0
        fun setPosiStaff(pos: Int) {
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
        var upImageProduct = ""
        var upImageBitmapProduct: Bitmap? = null

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
        var upImageStaff = ""
        var upImageBitmapStaff: Bitmap? = null

        lateinit var recyclerView: RecyclerView

        fun encodeImage(bm: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            ProductAdd.imageString = Base64.encodeToString(b, Base64.DEFAULT)
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun decodeImage(bm: String): Bitmap? {
            val decodedString: ByteArray = Base64.decode(bm, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            ProductAdd.imageBitmap = decodedByte
            return decodedByte
        }
    }

}