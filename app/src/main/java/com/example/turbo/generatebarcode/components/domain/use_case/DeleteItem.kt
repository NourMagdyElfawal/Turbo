package com.example.turbotools.generate_barcode_feature.domain.use_case

import com.example.turbotools.generate_barcode_feature.domain.model.Item
import com.example.turbotools.generate_barcode_feature.domain.repository.ItemRepository

class DeleteItem(
    private val repository:ItemRepository
    ) {
    suspend operator fun invoke(item: Item){
        repository.deleteItem(item)
    }
}