package com.example.turbotools.generate_barcode_feature.domain.use_case

import android.nfc.Tag
import android.util.Log
import com.example.turbotools.generate_barcode_feature.domain.model.Item
import com.example.turbotools.generate_barcode_feature.domain.repository.ItemRepository
import com.example.turbotools.generate_barcode_feature.domain.util.ItemOrder
import com.example.turbotools.generate_barcode_feature.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetItem(
    private val repository: ItemRepository
) {
    //order the item using time by default descending
    operator fun invoke(itemOrder: ItemOrder=ItemOrder.Date(OrderType.Descending)
    ): Flow<List<Item>>{
        return repository.getItems().map {items ->
            when(itemOrder.orderType) {
                is OrderType.Ascending -> {
                    when (itemOrder) {
                        is ItemOrder.Date -> items.sortedBy { it.timestamp }
//                        is ItemOrder.Inventory-> it.sortedBy {  Log.d("Tag","Inventory Ascending") }
//                        is ItemOrder.SubInventory->it.sortedBy {Log.d("Tag","SubInventory Ascending")}
//                        is ItemOrder.Locator->it.sortedBy { Log.d("Tag","Locator Ascending") }


                    }
                }

                is OrderType.Descending->{
                    when(itemOrder){
                        is ItemOrder.Date->items.sortedByDescending { it.timestamp }
//                        is ItemOrder.Inventory->Log.d("Tag","Inventory Descending")
//                        is ItemOrder.SubInventory->Log.d("Tag","SubInventory Descending")
//                        is ItemOrder.Locator->Log.d("Tag","Locator Descending")

                    }

                }

            }
        }
    }
}