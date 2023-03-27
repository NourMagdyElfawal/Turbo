package com.example.turbotools.generate_barcode_feature.domain.util

sealed class OrderType{
    object Ascending:OrderType()
    object Descending:OrderType()
}
