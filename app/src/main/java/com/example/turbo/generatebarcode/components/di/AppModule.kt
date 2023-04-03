package com.example.turbo.generatebarcode.components.di

import android.app.Application
import androidx.room.Room
import com.example.turbotools.generate_barcode_feature.data.data_source.ItemDataBase
import com.example.turbotools.generate_barcode_feature.data.repository.ItemRepositoryImpl
import com.example.turbotools.generate_barcode_feature.domain.repository.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun provideItemDatabase(app:Application):ItemDataBase{
        return Room.databaseBuilder(
            app,
            ItemDataBase::class.java,
            "item_db"
        ).build()
    }
    @Provides
    @Singleton
fun provideItemRepository(db:ItemDataBase):ItemRepository{
    return ItemRepositoryImpl(db.itemDao)
}
}