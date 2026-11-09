package com.miguelrodriguez19.safecube.core.ui.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object SafeCubeShape {
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val Full = CircleShape
}

val SafeCubeShapes: Shapes = Shapes(
    extraSmall = SafeCubeShape.ExtraSmall,
    small = SafeCubeShape.Small,
    medium = SafeCubeShape.Medium,
    large = SafeCubeShape.Large,
    extraLarge = SafeCubeShape.Large,
)

object SafeCubeSpacing {
    val None: Dp = 0.dp
    val ExtraSmall: Dp = 4.dp
    val Small: Dp = 8.dp
    val Medium: Dp = 12.dp
    val Large: Dp = 16.dp
    val ExtraLarge: Dp = 24.dp
    val Huge: Dp = 32.dp
    val TouchTarget: Dp = 48.dp
}

object SafeCubeElevation {
    val Level0: Dp = 0.dp
    val Level1: Dp = 1.dp
    val Level2: Dp = 3.dp
    val Level3: Dp = 6.dp
}

object SafeCubeMotion {
    val Instant: Duration = 0.milliseconds
    val Short: Duration = 150.milliseconds
    val Standard: Duration = 250.milliseconds
    val Long: Duration = 350.milliseconds

    val Enter: Duration = Standard
    val Exit: Duration = Short
    val StateChange: Duration = Short
    val Feedback: Duration = Short

    val EnterEasing: Easing = FastOutSlowInEasing
    val ExitEasing: Easing = FastOutLinearInEasing
    val EmphasizedEasing: Easing = FastOutSlowInEasing
}

object SafeCubeIconSize {
    val Small: Dp = 16.dp
    val Medium: Dp = 20.dp
    val Default: Dp = 24.dp
    val Large: Dp = 32.dp
}
