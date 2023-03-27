package com.example.turbotools.generate_barcode_feature.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(val itemScannedBarcode:String,
           val itemSerialNumber:Int,
           val itemCode:Int,
           val itemDescription:String,
           val itemUOM:String,
           val itemScannedQR_Code:String,
           val timestamp:Long,
                @PrimaryKey val id: Int? = null
)
class InvalidNoteException(message: String): Exception(message)

