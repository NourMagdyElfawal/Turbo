package com.example.turbo.generatebarcode.components.presentation

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.turbo.generatebarcode.components.StateListener

class GenerateBarcodeViewModel:ViewModel(
) {
var itemScannedBarcode:String?=""
var itemImageBarcodeBitmap: Bitmap?=null

var stateListener:StateListener?=null




    fun onGenerateBarcodeClick(view:View){
        if (isitemScannedBarcodeValid(itemScannedBarcode!!)){
            stateListener!!.onSuccess()
            return
        }else{
            stateListener!!.onFailure("please enter at least 8 digits")


        }
    }

    fun onPrintClick(view: View){
        if(itemImageBarcodeBitmap!!.toString().isEmpty()){
            stateListener!!.onFailure("error")
            return

        }else{

        }
    }

    private fun isitemScannedBarcodeValid(itemScannedBarcode: String): Boolean {
        return itemScannedBarcode.length > 7
    }

}