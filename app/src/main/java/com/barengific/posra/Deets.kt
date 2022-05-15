package com.barengific.posra

class Deets {
    companion object{
        var arrr: MutableList<Basket> = mutableListOf(Basket(0, "", "", "", ""))
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