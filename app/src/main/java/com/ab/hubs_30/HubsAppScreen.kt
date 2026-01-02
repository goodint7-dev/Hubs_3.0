package com.ab.hubs_30

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    colors: SliderColors
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
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxWidth,
                        )
                    )
                    layout(placeable.height, placeable.width) {
                        placeable.place(x = -placeable.width, y = 0)
                    }
                }
                .width(maxHeight)
                .height(48.dp)
        )
    }
}

fun calcSteps(min: Float, max: Float, stepSize: Float): Int {
    val steps = ((max - min) / stepSize) + 0.000001f
    return steps.toInt() - 1
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubsAppScreen(
    navController: NavController,
    hubsViewModel: HubsViewModel
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
    val maxWidth = 80f
    val minSlope = 0.5f
    val maxSlope = 5.0f

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)
        .safeDrawingPadding()
    ) {
        // Manual Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray) // Changed from MaterialTheme.colorScheme.primary
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hubs 3.0", style = MaterialTheme.typography.titleMedium, color = Color.White) // Changed color
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { navController.navigate("info") }) {


                Icon(
                    painter = painterResource(id = R.drawable.icon_i),
                    contentDescription = "Information",
                    tint = Color.White
                    )
            }

        }

        // Main Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Use weight instead of fillMaxSize to fill remaining space
        ) {
            Column(
                modifier = Modifier
                    .weight(10f)
                    .fillMaxHeight()
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
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hub Depth: ${ hubsViewModel.formatToFraction(calculatedHubDepth)} Inches",
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
                        .fillMaxHeight(0.8f)
                        .width(60.dp)
                )
            }

        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: HubsViewModel = viewModel()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { HubsAppScreen(
            hubsViewModel = sharedViewModel,
            navController = navController) }
        composable("info") { InfoScreen(navController = navController) }
    }
}

@Composable
fun InfoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // This makes the column scrollable
    ) {
        Text(
            text = stringResource(id = R.string.info_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.info_p1),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.info_title_2),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.info_p2),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.info_back_button))
        }
    }
}
