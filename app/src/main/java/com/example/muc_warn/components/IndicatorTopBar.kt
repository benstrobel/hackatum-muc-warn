package com.example.muc_warn.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorTopBar(isNetworkAvailable: MutableState<Boolean>, title: String) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth().padding(10.dp),title = {
            Text(text = title) },
        actions = { StatusIndicator(isNetworkAvailable = isNetworkAvailable) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun StatusIndicator(isNetworkAvailable: MutableState<Boolean>) {
    val color = if (isNetworkAvailable.value) Color(0xFF9BE6CB) else Color(0xFFEEAD92)

    Canvas(modifier = Modifier.size(25.dp), onDraw = {
        drawCircle(color = color)
    })
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable

fun IdicatorTopBarPreview() {
    IndicatorTopBar(mutableStateOf(false), "Status")
}