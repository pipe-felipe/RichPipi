package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.pipe.richpipi.form.TransactionalViewModel
import data.local.database.DatabaseProvider
import data.repository.ItemRepositoryImpl
import domain.usecase.AddItemUseCase
import domain.usecase.GetAllItemsUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.provideDatabase(this)
        val repo = ItemRepositoryImpl(db.itemDao())
        val addItemUseCase = AddItemUseCase(repo)
        val getAllItemsUseCase = GetAllItemsUseCase(repo)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TransactionalViewModel(addItemUseCase, getAllItemsUseCase) as T
            }
        }

        // Obtain ViewModel instance scoped to this Activity using the factory
        val vm = ViewModelProvider(this, factory).get(TransactionalViewModel::class.java)

        setContent {
            App(transactionalViewModel = vm)
        }

    }
}
