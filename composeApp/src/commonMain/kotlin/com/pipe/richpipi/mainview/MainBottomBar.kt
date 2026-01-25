package com.pipe.richpipi.mainview

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pipe.richpipi.ui.theme.dockBackground
import org.jetbrains.compose.resources.painterResource
import richpipi.composeapp.generated.resources.Res
import richpipi.composeapp.generated.resources.add_item
import richpipi.composeapp.generated.resources.save_icon

@Composable
fun MainBottomBar(
    onAddButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onRestoreButtonClick: () -> Unit,
    onLoginButtonClick: () -> Unit,
    isAuthenticated: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                    spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.dockBackground,
            tonalElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DockIcon(
                    painter = painterResource(Res.drawable.save_icon),
                    contentDescription = "Login",
                    onClick = onLoginButtonClick,
                )
                DockIcon(
                    painter = painterResource(Res.drawable.save_icon),
                    contentDescription = "Save",
                    onClick = onSaveButtonClick,
                    enabled = isAuthenticated,
                )
                DockIcon(
                    painter = painterResource(Res.drawable.save_icon),
                    contentDescription = "Restore",
                    onClick = onRestoreButtonClick,
                    enabled = isAuthenticated,
                )
                DockIcon(
                    painter = painterResource(Res.drawable.add_item),
                    contentDescription = "Add",
                    onClick = onAddButtonClick,
                )
            }
        }
    }
}

@Composable
private fun DockIcon(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "dockIconScale",
    )

    val alpha = if (enabled) 1f else 0.4f

    Box(
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(enabled) {
                detectTapGestures(
                    onPress = {
                        if (enabled) {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        } else {
                            onClick()
                        }
                    },
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 0.dp),
        )
    }
}
