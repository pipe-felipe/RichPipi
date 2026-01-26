package com.pipe.richpipi.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import domain.model.BackupResult
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.form_close_button_description
import richpipi.composeapp.generated.resources.save_dialog_description
import richpipi.composeapp.generated.resources.save_dialog_title

@Composable
fun SaveDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    backupResult: BackupResult?,
    onClearResult: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onClearResult()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        SaveDialogContent(
            onDismiss = {
                onClearResult()
                onDismiss()
            },
            onSave = onSave,
            backupResult = backupResult,
        )
    }
}

@Composable
private fun SaveDialogContent(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    backupResult: BackupResult?,
) {
    var isLoading by remember { mutableStateOf(false) }

    // Atualiza o estado baseado no backupResult
    val resultMessage = when (backupResult) {
        is BackupResult.Success -> "Seu dado foi salvo com sucesso! 😊"
        is BackupResult.Error -> "Erro: ${backupResult.message}"
        is BackupResult.SignInRequired -> "Aguardando autenticação..."
        null -> null
    }
    val isSuccess = backupResult is BackupResult.Success

    // Para de mostrar loading quando recebe um resultado
    LaunchedEffect(backupResult) {
        if (backupResult != null) {
            isLoading = false
        }
    }

    // Auto-dismiss dialog after successful backup
    LaunchedEffect(backupResult) {
        if (backupResult is BackupResult.Success) {
            delay(1500) // Small delay so user can see the success message
            onDismiss()
        }
    }

    BoxWithConstraints {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SaveDialogHeader()

                    // Show result message if available
                    resultMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        SaveButton(
                            onClick = {
                                isLoading = true
                                onSave()
                            },
                            enabled = !isLoading,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                CloseButton(
                    onDismiss = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
        }
    }
}

@Composable
private fun SaveDialogHeader() {
    Text(
        text = stringResource(Res.string.save_dialog_title),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp),
        textAlign = TextAlign.Center,
    )

    Text(
        text = stringResource(Res.string.save_dialog_description),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

@Composable
private fun SaveButton(onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = stringResource(Res.string.save_dialog_title),
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(stringResource(Res.string.save_dialog_title))
    }
}

@Composable
private fun CloseButton(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onDismiss,
        modifier = modifier,
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = stringResource(Res.string.form_close_button_description),
        )
    }
}
