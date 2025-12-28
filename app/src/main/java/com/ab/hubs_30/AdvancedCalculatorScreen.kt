package com.ab.hubs_30

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ValidatingNumericalInput(
    label: String,
    initialValue:Float,
    onValidValueChange: (Float) -> Unit,
    mustBePositive: Boolean = false
){
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValue.toString(),
                selection = TextRange.Zero
            )
        )
    }
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newTextFieldValue ->
            textFieldValue = newTextFieldValue
            val safeFloat = newTextFieldValue.text.toFloatOrNull()
            if (safeFloat != null) {
                if (mustBePositive && safeFloat < 0f) {
                    //do nothing with negative numbers
                } else {
                    onValidValueChange(safeFloat)
                }
            }
        },
        label = { Text(label)},
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                val text = textFieldValue.text
                textFieldValue = textFieldValue.copy(
                    selection = TextRange(0, text.length)
                )
            }
        }
    )
}


@Composable
fun AdvancedCalculatorScreen(
    navController: NavController,
    hubsViewModel: HubsViewModel
) {
    // Local state for the text fields, initialized from the ViewModel

    val currentLiftValue by hubsViewModel.liftValue.collectAsStateWithLifecycle()
    val currentSlopeValue by hubsViewModel.slopeValue.collectAsStateWithLifecycle()
    val currentWidthValue by hubsViewModel.widthValue.collectAsStateWithLifecycle()
    val calculatedHubDepth by hubsViewModel.hubDepthResult.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(50f)
                .fillMaxHeight()
        ) {
            Text("Special Calculator", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            ValidatingNumericalInput(
                label= "Lift (in)",
                initialValue = currentLiftValue,
                onValidValueChange = { newValue ->
                    hubsViewModel.updateLiftValue(newValue)
                }
            )
            Spacer(Modifier.height(16.dp))

            ValidatingNumericalInput(
                label= "Slope (%)",
                initialValue = currentSlopeValue,
                onValidValueChange = { newValue ->
                    hubsViewModel.updateSlopeValue(newValue)
                }
            )
            Spacer(Modifier.height(16.dp))

            ValidatingNumericalInput(
                label= "Width (Ft)",
                initialValue = currentWidthValue,
                onValidValueChange = { newValue ->
                    hubsViewModel.updateWidthValue(newValue)
                }
            )
            Spacer(Modifier.height(24.dp))
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(50f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hub Depth",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))

            // Formatted result text
            Text(
                text = hubsViewModel.formatToFraction(calculatedHubDepth),
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "Inches",
                style = MaterialTheme.typography.bodyMedium
            )


            // This spacer pushes the Back button to the bottom of the column
            Spacer(Modifier.weight(1f))

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}
