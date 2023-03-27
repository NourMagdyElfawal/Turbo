package com.example.turbotools.generate_barcode_feature.domain.repository

import com.example.turbotools.generate_barcode_feature.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {


    fun getItems(): Flow<List<Item>>

    suspend fun getItemById(int: Int): Flow<Item>

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(item: Item)
}