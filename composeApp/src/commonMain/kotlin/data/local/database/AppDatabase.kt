package data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import data.local.dao.TransactionDao
import data.local.entity.Converters
import data.local.entity.MonthlySummaryEntity
import data.local.entity.RecurringTransactionEntity
import data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class,
        RecurringTransactionEntity::class,
        MonthlySummaryEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
