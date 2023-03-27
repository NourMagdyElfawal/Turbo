package com.example.turbotools.generate_barcode_feature.domain.util

sealed class ItemOrder(val orderType: OrderType){
    class Date(orderType: OrderType):ItemOrder(orderType)

}
