package com.pipe.richpipi.data.mapper

import com.pipe.richpipi.data.local.Transaction as LocalTransaction
import com.pipe.richpipi.domain.model.Transaction as DomainTransaction

fun LocalTransaction.toDomain(): DomainTransaction {
    return DomainTransaction(
        id = id,
        name = name,
        amount = amount,
        date = date
    )
}

fun DomainTransaction.fromDomain(): LocalTransaction {
    return LocalTransaction(
        id = id,
        name = name,
        amount = amount,
        date = date
    )
}
