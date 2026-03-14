package com.example.weatherforecast.view.alertScreens.alertScreen.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.alert.AlertRepoInterface
import com.example.weatherforecast.data.alert.model.Alert
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertViewModel(
    private val repo: AlertRepoInterface
) : ViewModel() {

    val alerts: StateFlow<List<Alert>> = repo.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repo.deleteAlert(alert)
        }
    }

    fun updateAlertActivation(alertId: Int, isActive: Boolean) {
        viewModelScope.launch {
            repo.updateAlertActivation(alertId, isActive)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AlertViewModelFactory(val alertRepo: AlertRepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(alertRepo) as T

    }
}