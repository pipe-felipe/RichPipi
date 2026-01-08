package com.pipe.richpipi.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.form_category_label
import richpipi.composeapp.generated.resources.form_close_button_description
import richpipi.composeapp.generated.resources.form_notes_label
import richpipi.composeapp.generated.resources.form_quantity_label
import richpipi.composeapp.generated.resources.form_submit_button
import richpipi.composeapp.generated.resources.form_title

@Composable
fun TransactionalDialog(
    viewModel: TransactionalViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.form_title),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categoria
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = viewModel::onCategoryChange,
                        label = { Text(stringResource(Res.string.form_category_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quantidade
                    OutlinedTextField(
                        value = uiState.quantity,
                        onValueChange = viewModel::onQuantityChange,
                        label = { Text(stringResource(Res.string.form_quantity_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        isError = uiState.isQuantityError
                    )

                    if (uiState.isQuantityError) {
                        Text(
                            text = "Número inválido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Observações
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text(stringResource(Res.string.form_notes_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.submit()
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !uiState.isQuantityError
                    ) {
                        Text(stringResource(Res.string.form_submit_button))
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(
                            Res.string.form_close_button_description
                        )
                    )
                }
            }
        }
    }
}
