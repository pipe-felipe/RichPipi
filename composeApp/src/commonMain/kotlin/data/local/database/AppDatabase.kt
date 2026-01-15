package data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.TransactionDao
import data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
