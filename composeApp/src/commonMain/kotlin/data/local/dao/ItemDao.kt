package data.local.dao

import data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Int): ItemEntity?

    @Insert
    suspend fun addItem(item: ItemEntity): Long
}
