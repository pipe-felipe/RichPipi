package com.pipe.richpipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pipe.richpipi.form.TransactionalViewModel
import data.local.database.DatabaseProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.provideDatabase(this)

        // Obtain ViewModel instance scoped to this Activity
        val vm = ViewModelProvider(this).get(TransactionalViewModel::class.java)

        // Provide handler for adding items from the ViewModel
        vm.setAddItemHandler { item ->
            db.itemDao().addItem(item)
        }

        // Collect DB flow and push items to the ViewModel so the UI can observe them
        lifecycleScope.launch {
            db.itemDao().getAllItems().collect { list ->
                vm.setItems(list)
            }
        }

        setContent {
            App()
        }

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}