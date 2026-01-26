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
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.form_close_button_description
import richpipi.composeapp.generated.resources.restore_dialog_auth_required
import richpipi.composeapp.generated.resources.restore_dialog_description
import richpipi.composeapp.generated.resources.restore_dialog_error
import richpipi.composeapp.generated.resources.restore_dialog_loading
import richpipi.composeapp.generated.resources.restore_dialog_no_backups
import richpipi.composeapp.generated.resources.restore_dialog_select_backup
import richpipi.composeapp.generated.resources.restore_dialog_success
import richpipi.composeapp.generated.resources.restore_dialog_title

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
        is ImportResult.Success -> stringResource(Res.string.restore_dialog_success, restoreResult.importedCount)
        is ImportResult.Error -> stringResource(Res.string.restore_dialog_error, restoreResult.message)
        is ImportResult.SignInRequired -> stringResource(Res.string.restore_dialog_auth_required)
        is ImportResult.NoBackupsFound -> stringResource(Res.string.restore_dialog_no_backups)
        null -> null
    }
    val isSuccess = restoreResult is ImportResult.Success

    LaunchedEffect(restoreResult) {
        if (restoreResult is ImportResult.Success) {
            delay(1500)
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
                            text = stringResource(Res.string.restore_dialog_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    } else if (availableBackups.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.restore_dialog_select_backup),
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
            text = stringResource(Res.string.restore_dialog_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }

    Text(
        text = stringResource(Res.string.restore_dialog_description),
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
            contentDescription = stringResource(Res.string.form_close_button_description),
        )
    }
}
