package com.example.sharedflow.presentation

import android.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun WeatherScreen(modifier: Modifier = Modifier,
                  viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cities by viewModel.citySuggestions.collectAsStateWithLifecycle(emptyList())
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { message ->
            snackBarHostState.showSnackbar(
                message = message,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short)
        }
    }

    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackBarHostState) }) {paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)) {
            OutlinedTextField(
                value = state.selectedCityName,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Enter city name..") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .border(width = 2.dp, color = Color.Black)
            ) {
                items(cities) { city ->
                    val locationDetail = buildString {
                        append(city.name)
                        if (!city.state.isNullOrEmpty()) append(", ${city.state}")
                        append(", ${city.country}")
                    }
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onCitySelected(city)}
                        .padding(12.dp)) {
                        Text(
                            text = locationDetail,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "lat: ${city.latitude}, lon: ${city.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider()
                }

            }
            if (state.isLoading) CircularProgressIndicator()
            state.data?.let {
                Text(text = "Weather in ${state.selectedCityName}: ${it.temperature}", style = MaterialTheme.typography.displaySmall)
            }
        }
    }

}