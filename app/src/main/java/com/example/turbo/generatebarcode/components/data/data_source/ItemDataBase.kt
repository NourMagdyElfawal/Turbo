package com.example.turbotools.generate_barcode_feature.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.turbotools.generate_barcode_feature.domain.model.Item

@Database(
    entities = [Item::class],
    version = 1
)
abstract class ItemDataBase:RoomDatabase() {

    abstract val itemDao:ItemDao

    companion object{
        const val DATABASE_NAME="items_db"

    }

}