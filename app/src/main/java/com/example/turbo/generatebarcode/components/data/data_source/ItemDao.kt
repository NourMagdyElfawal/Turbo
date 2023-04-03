package com.example.turbotools.generate_barcode_feature.data.data_source

import androidx.compose.runtime.internal.DecoyImplementation
import androidx.room.*
import com.example.turbotools.generate_barcode_feature.domain.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getItems():Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id = :id")
     fun getItemById(id: Int):  Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)
}