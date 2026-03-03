package com.example.sharedflow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharedflow.domain.City
import com.example.sharedflow.domain.WeatherRepository
import com.example.sharedflow.domain.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository) : ViewModel() {

        private val _uiState = MutableStateFlow(WeatherState())
    val uiState = _uiState.asStateFlow()

    private val _citySuggestions = MutableSharedFlow<List<City>>(replay = 1)
    val citySuggestions = _citySuggestions.asSharedFlow()

    private val _errorEvents = Channel<String>()
    val errorEvents = _errorEvents.receiveAsFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(selectedCityName = query) }

        searchJob?.cancel()

        if(query.lowercase() == "error") {
            viewModelScope.launch {
                _errorEvents.send("Manual Error Triggered: 'error is a forbidden city!")

            }
            return
        }

        if (query.length < 3) {
            viewModelScope.launch { _citySuggestions.emit(emptyList()) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // debounce timer
            repository.searchCity(query).onSuccess { cities ->
                _citySuggestions.emit(cities)
            }.onFailure {
                _errorEvents.send("Network Error: Check your connection!")
            }
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            _citySuggestions.emit(emptyList())

            val displayName = city.name

            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedCityName = displayName
                ) }

            repository.fetchWeather(city.latitude, city.longitude)
                .onSuccess { weather ->
                    _uiState.update {
                        it.copy(
                            data = weather,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _errorEvents.send("Failed to load weather for ${displayName}")
                }
        }
    }
}