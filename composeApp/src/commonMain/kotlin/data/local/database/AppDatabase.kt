package data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.ItemDao
import data.local.entity.ItemEntity

@Database(entities = [ItemEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
