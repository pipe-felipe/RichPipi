package data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "richpipi-db"
        ).fallbackToDestructiveMigration(true).build()
    }
}
