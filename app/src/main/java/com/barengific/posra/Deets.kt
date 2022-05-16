package com.barengific.posra

import com.barengific.posra.product.Product
import com.barengific.posra.staff.Staff

class Deets {
    companion object{
        var arrBasket: MutableList<Basket> = mutableListOf(Basket(0, "", "", "", ""))
        var arrProduct: MutableList<Product> = mutableListOf(Product(0, "", "", "", "","","","",""))
        var arrStaff: MutableList<Staff> = mutableListOf(Staff(0, "", "", "", "","","","",""))
        var bc_value = ""

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
    }
}