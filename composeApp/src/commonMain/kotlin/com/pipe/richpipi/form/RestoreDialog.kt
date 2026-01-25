package com.pipe.richpipi.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import domain.model.ImportResult
import domain.model.SpreadsheetFile

@Composable
fun RestoreDialog(
    onDismiss: () -> Unit,
    onRestore: (String) -> Unit,
    onLoadBackups: () -> Unit,
    availableBackups: List<SpreadsheetFile>,
    restoreResult: ImportResult?,
    isLoading: Boolean,
    onClearResult: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onLoadBackups()
    }

    Dialog(
        onDismissRequest = {
            onClearResult()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        RestoreDialogContent(
            onDismiss = {
                onClearResult()
                onDismiss()
            },
            onRestore = onRestore,
            availableBackups = availableBackups,
            restoreResult = restoreResult,
            isLoading = isLoading,
        )
    }
}

@Composable
private fun RestoreDialogContent(
    onDismiss: () -> Unit,
    onRestore: (String) -> Unit,
    availableBackups: List<SpreadsheetFile>,
    restoreResult: ImportResult?,
    isLoading: Boolean,
) {
    val resultMessage = when (restoreResult) {
        is ImportResult.Success -> "Backup restaurado com sucesso! ${restoreResult.importedCount} transações importadas. 😊"
        is ImportResult.Error -> "Erro: ${restoreResult.message}"
        is ImportResult.SignInRequired -> "Autenticação necessária..."
        is ImportResult.NoBackupsFound -> "Nenhum backup encontrado."
        null -> null
    }
    val isSuccess = restoreResult is ImportResult.Success

    BoxWithConstraints {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.85f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RestoreDialogHeader()

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
                    } else if (availableBackups.isEmpty() && restoreResult !is ImportResult.NoBackupsFound) {
                        Text(
                            text = "Carregando backups...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    } else if (availableBackups.isNotEmpty()) {
                        Text(
                            text = "Selecione um backup para restaurar:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        ) {
                            items(availableBackups) { backup ->
                                BackupItem(
                                    backup = backup,
                                    onClick = { onRestore(backup.id) },
                                )
                            }
                        }
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
private fun RestoreDialogHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Restore,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = "Restaurar Backup",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }

    Text(
        text = "Selecione um backup do Google Drive para restaurar seus dados. Atenção: os dados atuais serão substituídos.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

@Composable
private fun BackupItem(
    backup: SpreadsheetFile,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = backup.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            backup.createdTime?.let { time ->
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
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
            contentDescription = "Fechar",
        )
    }
}
