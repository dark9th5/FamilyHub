package com.family.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.family.app.ui.theme.FamilyElevation
import com.family.app.ui.theme.FamilyRadius
import com.family.app.ui.theme.FamilySpacing

// Mesh Gradient colors
// Mesh-like Gradient colors
private val MESH_BLUE = Color(0xFFD4EBF8)
private val MESH_PINK = Color(0xFFEDD7E3)
private val MESH_BLUE_LIGHT = Color(0xFFE8F5FB)
private val MESH_PINK_LIGHT = Color(0xFFF5E9EF)
private val MESH_TEXT_DARK = Color(0xFF2F4A5A)

@Composable
fun PremiumScreenBackground(content: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        cs.background,
                        cs.surfaceContainer,
                        cs.background
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 10f), 0f)
            drawCircle(
                color = cs.primary.copy(alpha = 0.06f),
                radius = size.minDimension * 0.65f,
                center = Offset(size.width * 0.92f, size.height * 0.08f)
            )
            drawCircle(
                color = cs.secondary.copy(alpha = 0.05f),
                radius = size.minDimension * 0.58f,
                center = Offset(size.width * 0.14f, size.height * 0.9f)
            )
            drawCircle(
                color = cs.tertiary.copy(alpha = 0.04f),
                radius = size.minDimension * 0.46f,
                center = Offset(size.width * 0.52f, size.height * 0.48f)
            )
            drawLine(
                color = cs.outlineVariant.copy(alpha = 0.18f),
                start = Offset(size.width * 0.06f, size.height * 0.18f),
                end = Offset(size.width * 0.94f, size.height * 0.18f),
                strokeWidth = 2f,
                pathEffect = dash
            )
            drawLine(
                color = cs.outlineVariant.copy(alpha = 0.12f),
                start = Offset(size.width * 0.12f, size.height * 0.76f),
                end = Offset(size.width * 0.88f, size.height * 0.76f),
                strokeWidth = 2f,
                pathEffect = dash
            )
        }
        content()
    }
}

@Composable
fun HeroHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FamilySpacing.xxs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.widthIn(min = 96.dp)
                ) {
                    Text(
                        text = "FAMILY HUB",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = FamilySpacing.xs, vertical = FamilySpacing.xxs)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.48f)
                .height(6.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MESH_BLUE, MESH_BLUE_LIGHT, MESH_PINK),
                    ),
                    RoundedCornerShape(8.dp)
                )
        )
    }
}

@Composable
fun PremiumCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FamilyRadius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = FamilyElevation.medium)
    ) {
        Column(
            modifier = Modifier.padding(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.sm)
        ) {
            content()
        }
    }
}

@Composable
fun PremiumInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(FamilyRadius.sm),
        leadingIcon = leadingIcon?.let { icon -> { Icon(icon, contentDescription = null) } },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.72f)
        )
    )
}

@Composable
fun PrimaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f, label = "primary_scale")

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(FamilyRadius.sm),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(MESH_BLUE, MESH_BLUE_LIGHT, MESH_PINK),
                    ),
                    RoundedCornerShape(FamilyRadius.sm)
                )
                .border(
                    BorderStroke(1.dp, MESH_TEXT_DARK.copy(alpha = 0.12f)),
                    RoundedCornerShape(FamilyRadius.sm)
                )
                .padding(vertical = 14.dp, horizontal = FamilySpacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge, color = MESH_TEXT_DARK)
        }
    }
}

@Composable
fun SecondaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(FamilyRadius.sm),
        border = BorderStroke(1.2.dp, Color(0xFFD4EBF8)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MESH_BLUE_LIGHT.copy(alpha = 0.85f),
            contentColor = MESH_TEXT_DARK
        ),
        contentPadding = PaddingValues(horizontal = FamilySpacing.md, vertical = FamilySpacing.xs)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun TertiaryGhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StatusChip(text: String, isOnline: Boolean = true) {
    val color = if (isOnline) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = color.copy(alpha = 0.13f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.42f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = FamilySpacing.sm, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(7.dp).background(color, CircleShape))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

@Composable
fun AvatarInitials(name: String, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = modifier
            .size(size)
            .background(
                Brush.radialGradient(listOf(MESH_BLUE, MESH_PINK)),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(initial, color = MESH_TEXT_DARK, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GradientMetricCard(
    title: String,
    value: String,
    gradient: Brush,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(FamilyRadius.md),
        color = Color.Transparent,
        shadowElevation = FamilyElevation.medium
    ) {
        Box(
            modifier = Modifier
                .background(gradient, RoundedCornerShape(FamilyRadius.md))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(FamilyRadius.md))
                .padding(FamilySpacing.sm)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(FamilySpacing.xxs)) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Text(value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(title, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun EmptyStateCard(title: String, subtitle: String, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = FamilySpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FamilySpacing.xs)
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(listOf(MESH_BLUE, MESH_PINK)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AcUnit, contentDescription = null, tint = MESH_TEXT_DARK)
            }
        }
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (actionText != null && onAction != null) {
            SecondaryActionButton(text = actionText, onClick = onAction)
        }
    }
}

@Composable
fun LoadingPulseCard() {
    val transition = rememberInfiniteTransition(label = "loading")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )
    val shimmer = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha),
            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = alpha * 1.2f),
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha)
        )
    )

    PremiumCard {
        Box(modifier = Modifier.fillMaxWidth(0.6f).height(18.dp).background(shimmer, RoundedCornerShape(FamilyRadius.sm)))
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(shimmer, RoundedCornerShape(FamilyRadius.sm)))
        Box(modifier = Modifier.fillMaxWidth(0.82f).height(12.dp).background(shimmer, RoundedCornerShape(FamilyRadius.sm)))
    }
}

@Composable
fun AnimatedEntrance(modifier: Modifier = Modifier, delayMillis: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(360, delayMillis = delayMillis)) +
            expandVertically(animationSpec = tween(360, delayMillis = delayMillis), expandFrom = Alignment.Top) +
            slideInVertically(animationSpec = tween(420, delayMillis = delayMillis), initialOffsetY = { it / 6 })
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumAlertDialog(
    title: String,
    body: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        PremiumCard {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                TertiaryGhostButton(text = dismissText, onClick = onDismiss, modifier = Modifier.weight(1f))
                PrimaryActionButton(text = confirmText, onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumBottomSheet(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = FamilyRadius.lg, topEnd = FamilyRadius.lg),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = FamilyElevation.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = FamilySpacing.lg, vertical = FamilySpacing.sm),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.sm)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}
