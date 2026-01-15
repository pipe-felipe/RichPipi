package data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create recurring_transactions table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `recurring_transactions` (
                  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                  `amountCents` INTEGER NOT NULL,
                  `type` TEXT NOT NULL,
                  `description` TEXT,
                  `startDate` INTEGER NOT NULL,
                  `rule` TEXT NOT NULL,
                  `interval` INTEGER NOT NULL,
                  `endDate` INTEGER,
                  `active` INTEGER NOT NULL,
                  `createdAt` INTEGER NOT NULL
                )
            """.trimIndent())

            // Create monthly_summaries table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `monthly_summaries` (
                  `year` INTEGER NOT NULL,
                  `month` INTEGER NOT NULL,
                  `totalIncomeCents` INTEGER NOT NULL,
                  `totalExpenseCents` INTEGER NOT NULL,
                  `startingBalanceCents` INTEGER NOT NULL,
                  `endingBalanceCents` INTEGER NOT NULL,
                  `updatedAt` INTEGER NOT NULL,
                  PRIMARY KEY(`year`,`month`)
                )
            """.trimIndent())
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add isRecurring column to transactions (default false)
            database.execSQL("ALTER TABLE `transactions` ADD COLUMN `isRecurring` INTEGER NOT NULL DEFAULT 0")
        }
    }

    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "richpipi-db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }
}
