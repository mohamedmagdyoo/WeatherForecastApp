package com.example.weatherforecast.view.alertScreens.addAlertScreen.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherforecast.data.alert.AlertRepoInterface
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlarmKind
import com.example.weatherforecast.data.alert.model.AlertType
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.alertScreens.workManager.AlarmWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.minutes

class AddAlertViewModel(
    private val context: Context,
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
        val timeNow = System.currentTimeMillis()
        if (alert.alertValue == null){
            _screenState.value = ScreenState.OnUnSelectedDate
            return
        }
        if (alert.startTime <= timeNow  ) {
            _screenState.value = ScreenState.OnSelectedDateWrong
            return
        }
        viewModelScope.launch {
            val alertId = repo.insertAlert(alert).getOrNull() ?: -1
            Log.d(AppConstants.TAG, "Inserted alarm with id: ${alertId}")
            registerAlarm(alertId, context)
            //Here i have to also scheduler the alarm with
            _screenState.value = ScreenState.OnSelectedDateCorrect
        }
    }

    fun registerAlarm(alarmId: Long, context: Context) {
        val runAfter = _alertState.value.startTime - System.currentTimeMillis()
        Log.d(AppConstants.TAG, "registerAlarm:Will Run After ${runAfter}")
        if (runAfter < 0)
            return

        val request = OneTimeWorkRequestBuilder<AlarmWorker>()
            .addTag("alert_$alarmId")
            .setInputData(workDataOf("alertId" to alarmId))
            .setInitialDelay(runAfter, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                uniqueWorkName = "alert_$alarmId",
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = request
            )
        Log.d(AppConstants.TAG, "registerAlarm: Done")
    }
}

sealed class ScreenState {
    object OnSelectedDateWrong : ScreenState()
    object OnUnSelectedDate : ScreenState()
    object OnSelectedDateCorrect : ScreenState()
    object Ideal : ScreenState()
}

class AddAlertViewModelFactory(
    private val context: Context,
    private val repo: AlertRepoInterface
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddAlertViewModel(context, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}