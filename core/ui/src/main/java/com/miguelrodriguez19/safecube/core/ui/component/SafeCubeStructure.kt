package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeElevation
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIconSize
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

@Composable
fun SafeCubeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    navigationContentDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    require(navigationIcon == null || !navigationContentDescription.isNullOrBlank()) {
        "navigationContentDescription is required when navigationIcon is provided"
    }
    val resolvedNavigationContentDescription = navigationContentDescription.orEmpty()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = SafeCubeElevation.Level0,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = SafeCubeSpacing.TouchTarget)
                .padding(
                    horizontal = SafeCubeSpacing.Small,
                    vertical = SafeCubeSpacing.Small,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationIcon?.let { icon ->
                SafeCubeIconButton(
                    imageVector = icon,
                    contentDescription = resolvedNavigationContentDescription,
                    onClick = onNavigationClick,
                    modifier = Modifier,
                )
                Spacer(modifier = Modifier.width(SafeCubeSpacing.Small))
            }
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
    }
}

data class SafeCubeNavigationItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
)

@Composable
fun SafeCubeBottomNavigation(
    items: List<SafeCubeNavigationItem>,
    selectedItemId: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(items.map(SafeCubeNavigationItem::id).toSet().size == items.size) {
        "SafeCubeNavigationItem ids must be unique"
    }
    require(items.any { it.id == selectedItemId }) {
        "selectedItemId must match an item id"
    }

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val selected = item.id == selectedItemId
            NavigationBarItem(
                selected = selected,
                onClick = { if (item.enabled) onItemClick(item.id) },
                enabled = item.enabled,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                    )
                },
                label = { Text(text = item.label) },
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = SafeCubeSpacing.TouchTarget,
                        minHeight = SafeCubeSpacing.TouchTarget,
                    )
                    .semantics {
                        role = Role.Tab
                        this.selected = selected
                    },
            )
        }
    }
}

@Composable
fun SafeCubeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardModifier = if (onClick == null) {
        modifier
    } else {
        modifier.defaultMinSize(
            minWidth = SafeCubeSpacing.TouchTarget,
            minHeight = SafeCubeSpacing.TouchTarget,
        )
    }
    val elevation = CardDefaults.cardElevation(defaultElevation = SafeCubeElevation.Level1)
    val paddedContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
            content = content,
        )
    }

    if (onClick == null) {
        Card(
            modifier = cardModifier,
            shape = MaterialTheme.shapes.medium,
            elevation = elevation,
            content = paddedContent,
        )
    } else {
        Card(
            onClick = { if (enabled) onClick() },
            modifier = cardModifier.semantics { role = Role.Button },
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            elevation = elevation,
            content = paddedContent,
        )
    }
}

@Composable
fun SafeCubeItemRow(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    icon: ImageVector? = null,
    stateDescription: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = SafeCubeSpacing.TouchTarget)
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier
                    .clickable(
                        enabled = enabled,
                        role = Role.Button,
                        onClick = { if (enabled) onClick() },
                    )
            },
        )
        .semantics(mergeDescendants = true) {
            if (stateDescription != null) {
                this.stateDescription = stateDescription
            }
        }

    Row(
        modifier = rowModifier.padding(
            horizontal = SafeCubeSpacing.Large,
            vertical = SafeCubeSpacing.Small,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(SafeCubeIconSize.Default),
            )
            Spacer(modifier = Modifier.width(SafeCubeSpacing.Small))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            stateDescription?.let {
                Row(
                    modifier = Modifier.clearAndSetSemantics {},
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        modifier = Modifier.size(SafeCubeIconSize.Medium),
                    )
                    Spacer(modifier = Modifier.width(SafeCubeSpacing.ExtraSmall))
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
