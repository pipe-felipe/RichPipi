package data.local.entity

import androidx.room.TypeConverter
import domain.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromRecurrenceRule(value: RecurrenceRule?): String? = value?.name

    @TypeConverter
    fun toRecurrenceRule(value: String?): RecurrenceRule? = value?.let { RecurrenceRule.valueOf(it) }
}
