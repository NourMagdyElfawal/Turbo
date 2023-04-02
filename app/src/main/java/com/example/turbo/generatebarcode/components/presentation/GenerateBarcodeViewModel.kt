package com.example.turbo.generatebarcode.components.presentation

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.turbo.generatebarcode.components.StateListener

class GenerateBarcodeViewModel:ViewModel(
) {
var itemScannedBarcode:String?=null
var itemImageBarcodeBitmap: Bitmap?=null

var stateListener:StateListener?=null




    fun onGenerateBarcodeClick(view:View){
        if (itemScannedBarcode!!.length<8){
            stateListener!!.onFailure("please enter at least 8 digits")
            return
        }else{

            stateListener!!.onSuccess()
        }
    }

    fun onPrintClick(view: View){
        if(itemImageBarcodeBitmap!!.toString().isEmpty()){
            stateListener!!.onFailure("error")
            return

        }else{

        }
    }
}