package com.example.turbo.generatebarcode.components.presentation.model

import android.graphics.Bitmap

class ItemModel {
    var  itemScannedBarcode:String?=""
    lateinit var  itemImageBarcodeBitmap:Bitmap
    var  itemStore:String?=""
    var  itemCity:String?=""
    var  itemUOM:String?=""
    var  itemScannedQR_Code:String?=""

}