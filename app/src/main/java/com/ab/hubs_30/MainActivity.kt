package com.ab.hubs_30

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

//setups for the viewmodel, saved state handle, and stateflow

private const val LIFT_VALUE_KEY = "lift_value"
private const val SLOPE_VALUE_KEY = "slope_Value"
private const val WIDTH_VALUE_KEY = "width_Value"

class HubsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val liftValue: StateFlow<Float> = savedStateHandle.getStateFlow(
        LIFT_VALUE_KEY, 9.0f)

    fun updateLiftValue(newValue: Float) {
        savedStateHandle[LIFT_VALUE_KEY] = newValue
    }

    val slopeValue: StateFlow<Float> = savedStateHandle.getStateFlow(
        SLOPE_VALUE_KEY, 2.0f)

    fun updateSlopeValue(newValue: Float) {
        savedStateHandle[SLOPE_VALUE_KEY] = newValue
    }

    val widthValue: StateFlow<Float> = savedStateHandle.getStateFlow(
        WIDTH_VALUE_KEY, 28.0f)


    fun updateWidthValue(newValue: Float) {
        savedStateHandle[WIDTH_VALUE_KEY] = newValue
    }
// with functions to update the saved state handle when the value changes

    //
    val hubDepthResult: StateFlow<Float> = combine(
        liftValue,
        slopeValue,
        widthValue
    ) { currentLift, currentSlope, currentWidth ->
        recalculateHubDepth(currentLift, currentSlope, currentWidth)
    } .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = recalculateHubDepth(
            lift = liftValue.value,
            slope = slopeValue.value,
            width = widthValue.value
        )
    )

    //
    private fun recalculateHubDepth(lift: Float, slope: Float, width: Float): Float {
        val halfWidth = width / 2f
        val calcSlope = slope / 100.0f
        val ctrHeight: Float = halfWidth * calcSlope
        val eLift = lift * 0.08333367f
        val hubDepth = (eLift - ctrHeight) * 12.0f
        Log.d("HubCalculations", "lift: $lift, slope: $slope, width: $width")
        Log.d("HubCalculations", "halfWidth: $halfWidth, calcSlope: $calcSlope, ctrHeight: $ctrHeight, eLift: $eLift")
        Log.d("HubCalculations", "hubDepth: $hubDepth")
        return hubDepth
     //   return eLift
    }
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors()
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = colors,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = 270f
                    // The transform origin is crucial for proper layout after rotation
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
                }
                .layout { measurable, constraints ->
                    // Swap the width and height constraints for the rotated slider
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxWidth,
                        )
                    )
                    // The layout system doesn't automatically handle the new size of the
                    // rotated composable, so we must manually specify the new width and height.
                    layout(placeable.height, placeable.width) {
                        // Place the composable, offsetting it to the left by its width to
                        // bring it back into the visible bounds of the parent Box.
                        placeable.place(-placeable.width, 0)
                    }
                }
                // After rotation and layout, the slider's logical "width" is now the
                // max height of its container, and its "height" is a fixed value.
                .width(maxHeight)
                .height(48.dp)
        )
    }
}

fun calcSteps(min: Float, max: Float, stepSize: Float): Int {
     val steps = ((max - min) / stepSize) + 0.000001f
return steps.toInt() - 1
}


@Composable
fun HubsAppScreen(
    //   modifier: Modifier = Modifier,
    hubsViewModel: HubsViewModel = viewModel()
) {
    val currentLiftValue by hubsViewModel.liftValue.collectAsStateWithLifecycle()
    val currentSlopeValue by hubsViewModel.slopeValue.collectAsStateWithLifecycle()
    val currentWidthValue by hubsViewModel.widthValue.collectAsStateWithLifecycle()
    val calculatedHubDepth by hubsViewModel.hubDepthResult.collectAsStateWithLifecycle()
    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.White,
        inactiveTrackColor = Color.Gray
    )
    val minLift = 1.0f
    val maxLift = 36.0f
    val minWidth = 10.0f
    val maxWidth = 80.0f
    val minSlope = 0.5f
    val maxSlope = 5.0f

//
//One box to rule them all
//
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(10f)
                .fillMaxHeight()
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

//
//Lift value text box here
//
            Text(
                text = "Lift\n ${ "%.1f".format(currentLiftValue)} \nInches",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
//
// vertical lift slider
// Refactored Vertical Slider for Lift
            VerticalSlider(
                value = currentLiftValue,
                onValueChange = { newValue -> hubsViewModel.updateLiftValue(newValue) },
                valueRange = minLift..maxLift,
                steps = calcSteps(minLift, maxLift, 0.5f),
                colors = sliderColors,
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .width(60.dp)
            )

        }
//
// Central column
//
        Column(
            modifier = Modifier
                .weight(80f)
                .fillMaxHeight()
                .background(Color.LightGray.copy(alpha = .6f))
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//
//Container for the center of screen
//This is where hub depth is displayed
//
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Takes full width of the Center Column
                    .weight(20f) // 1 part of (1 + 8 + 1 = 10 parts) -> 10% height of Center Column
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hub Depth: ${ "%.2f".format(calculatedHubDepth)} Inches",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Takes full width of the Center Column
                    .weight(60f) // 1 part of (1 + 8 + 1 = 10 parts) -> 10% height of Center Column
                    .background(Color.DarkGray.copy(alpha = 0.3f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val totalDisplayWidthInFeet = maxWidth + 2.0f
                    val xPixelsPerFoot = canvasWidth / totalDisplayWidthInFeet
                    val yPixelsPerInch = canvasHeight / (maxLift - minLift)
                    val xCenter = canvasWidth / 2

                    val halfRoadWidthInPixels = (currentWidthValue / 2.0f) * xPixelsPerFoot
                    val curbWidthInPixels = 1.0f * xPixelsPerFoot

                    val lCurbInside = xCenter - halfRoadWidthInPixels
                    val rCurbInside = xCenter + halfRoadWidthInPixels
                    val lCurbOutside = lCurbInside - curbWidthInPixels
                    val rCurbOutside = rCurbInside + curbWidthInPixels

                    val yBottom = canvasHeight
                    val yTop = yBottom - (currentLiftValue * yPixelsPerInch)
                    val yCenter = yTop + (calculatedHubDepth * yPixelsPerInch)

                    val leftCurb = Path().apply {
                        moveTo(lCurbInside, yTop)
                        lineTo(lCurbOutside, yTop)
                        lineTo(lCurbOutside, yBottom)
                        lineTo(lCurbInside, yBottom)
                        close()
                    }
                    drawPath(leftCurb, Color.White)

                    val rightCurb = Path().apply{
                        moveTo(rCurbInside, yTop)
                        lineTo(rCurbOutside, yTop)
                        lineTo(rCurbOutside, yBottom)
                        lineTo(rCurbInside, yBottom)
                        close()
                    }
                    drawPath(rightCurb, Color.White)

                    val gradeTriangle = Path().apply {
                        moveTo(lCurbInside, yBottom)
                        lineTo(xCenter, yCenter)
                        lineTo(rCurbInside, yBottom)
                        close()
                    }
                    drawPath(gradeTriangle, Color(0xFF8B4513))
                    drawLine(Color.Red, start = Offset(lCurbInside, yTop), end = Offset(rCurbInside, yTop))
                }
            }

//
// Width value text box and slider here
//
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Takes full width of the Center Column
                    .weight(20f) // 1 part of (1 + 8 + 1 = 10parts) -> 10% height of Center Column
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Width: ${ "%.1f".format(currentWidthValue)} Feet",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = currentWidthValue,
                    onValueChange = { newValue ->
                        hubsViewModel.updateWidthValue(newValue)
                    },
                    valueRange = minWidth..maxWidth,
                    steps = calcSteps(minWidth, maxWidth, 0.5f),
                    colors = sliderColors,
                    modifier = Modifier
                        .width(300.dp)
                        .height(48.dp)

                )
            }
        }
//
// Slope value text box and slider here
//
        Column(
            modifier = Modifier
                .weight(10f)
                .fillMaxHeight()
                .background(Color.LightGray.copy(alpha = .3f))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Slope\n ${ "%.1f".format(currentSlopeValue)} \nPercent",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            // Refactored Vertical Slider for Slope
            VerticalSlider(
                value = currentSlopeValue,
                onValueChange = { newValue -> hubsViewModel.updateSlopeValue(newValue) },
                valueRange = minSlope..maxSlope,
                steps = calcSteps(minSlope, maxSlope, 0.1f),
                colors = sliderColors,
                modifier = Modifier
                    .fillMaxHeight(0.8f) // Or .height(500.dp) as you had
                    .width(60.dp)
            )
        }

    }
}


class MainActivity : ComponentActivity() { // REMOVE @Composable and @ContentView from here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Hubs_30Theme {
            // Call your main screen composable here
            HubsAppScreen()
        }
    }
}
