package com.ab.hubs_30

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun recalculateHubDepth(lift: Float, slope: Float, width: Float): Float {
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