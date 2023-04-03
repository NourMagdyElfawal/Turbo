package com.example.turbo.generatebarcode.components.presentation

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.turbo.generatebarcode.components.StateListener
import com.example.turbotools.generate_barcode_feature.domain.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerateBarcodeViewModel @Inject constructor(
    private val repository: ItemRepository
) :ViewModel(
) {


val items=repository.getItems()
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


    private fun isitemScannedBarcodeValid(itemScannedBarcode: String): Boolean {
        return itemScannedBarcode.length > 7
    }

}