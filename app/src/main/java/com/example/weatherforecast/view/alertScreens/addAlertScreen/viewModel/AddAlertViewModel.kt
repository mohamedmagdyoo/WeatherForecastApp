package com.example.weatherforecast.view.alertScreens.addAlertScreen.viewModel


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.alert.AlertRepoInterface
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlarmKind
import com.example.weatherforecast.data.alert.model.AlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddAlertViewModel(
    private val repo: AlertRepoInterface
) : ViewModel() {

    private val _alertState = MutableStateFlow(Alert())
    val alertState: StateFlow<Alert> = _alertState
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Ideal)
    val screenState: StateFlow<ScreenState> = _screenState


    fun onAlertTypeChange(type: AlertType) {
        _alertState.update {
            it.copy(
                alertType = type,
                alertValue = if (type == AlertType.RAIN) null else it.alertValue
            )
        }
    }

    fun onAlertValueChange(value: Double) {
        _alertState.update { it.copy(alertValue = value) }
    }

    fun onStartTimeChange(time: Long) {
        _alertState.update { it.copy(startTime = time) }
    }

    fun onEndTimeChange(time: Long) {
        _alertState.update { it.copy(endTime = time) }
    }

    fun onAlarmKindChange(kind: AlarmKind) {
        _alertState.update { it.copy(alarmKind = kind) }
    }

    fun saveAlert() {
        val alert = _alertState.value
        if (alert.endTime <= alert.startTime) {
            _screenState.value = ScreenState.OnSelectedDateWrong
            return
        }
        viewModelScope.launch {
            repo.insertAlert(alert)
            //Here i have to also scheduler the alarm with
            _screenState.value = ScreenState.OnSelectedDateCorrect
        }
    }
}

sealed class ScreenState {
    object OnSelectedDateWrong : ScreenState()
    object OnSelectedDateCorrect : ScreenState()
    object Ideal : ScreenState()
}

class AddAlertViewModelFactory(
    private val repo: AlertRepoInterface
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddAlertViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}