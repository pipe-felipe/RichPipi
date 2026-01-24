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
import org.jetbrains.compose.resources.stringResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.form_close_button_description
import richpipi.composeapp.generated.resources.save_dialog_description
import richpipi.composeapp.generated.resources.save_dialog_title

@Composable
fun SaveDialog(
    onDismiss: () -> Unit,
    onSave: (onResult: (BackupResult) -> Unit) -> Unit,
    onSignInRequired: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        SaveDialogContent(
            onDismiss = onDismiss,
            onSave = onSave,
            onSignInRequired = onSignInRequired,
        )
    }
}

@Composable
private fun SaveDialogContent(
    onDismiss: () -> Unit,
    onSave: (onResult: (BackupResult) -> Unit) -> Unit,
    onSignInRequired: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

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
                                resultMessage = null
                                onSave { result ->
                                    isLoading = false
                                    when (result) {
                                        is BackupResult.Success -> {
                                            isSuccess = true
                                            resultMessage = "Seu dado foi salvo com sucesso! 😊"
                                        }
                                        is BackupResult.Error -> {
                                            isSuccess = false
                                            resultMessage = "Erro: ${result.message}"
                                        }
                                        is BackupResult.SignInRequired -> {
                                            resultMessage = "Por favor, faça login com sua conta Google."
                                            onSignInRequired()
                                        }
                                    }
                                }
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
