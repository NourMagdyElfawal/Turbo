package com.example.turbotools.generate_barcode_feature.data.repository

import com.example.turbotools.generate_barcode_feature.data.data_source.ItemDao
import com.example.turbotools.generate_barcode_feature.domain.model.Item
import com.example.turbotools.generate_barcode_feature.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(private val dao: ItemDao
):ItemRepository {
    override fun getItems(): Flow<List<Item>> {
        return dao.getItems()
    }

    override suspend fun getItemById(id: Int): Item? {
        return dao.getItemById(id)
    }

    override suspend fun insertItem(item: Item) {
         dao.insertItem(item)
    }

    override suspend fun deleteItem(item: Item) {
         dao.deleteItem(item)
    }
}