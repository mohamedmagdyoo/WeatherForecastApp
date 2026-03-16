package com.example.weatherforecast.view.alertScreens.addAlertScreen.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.labs.R
import com.example.weatherforecast.MyApplication
import com.example.weatherforecast.data.alert.AlertRepo
import com.example.weatherforecast.data.alert.dataSorce.local.AlertLocalDataSource
import com.example.weatherforecast.data.alert.model.AlarmKind
import com.example.weatherforecast.data.alert.model.AlertType
import com.example.weatherforecast.data.alert.model.toDateString
import com.example.weatherforecast.data.db.DataBaseHelper
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.view.alertScreens.addAlertScreen.viewModel.AddAlertViewModel
import com.example.weatherforecast.view.alertScreens.addAlertScreen.viewModel.AddAlertViewModelFactory
import com.example.weatherforecast.view.alertScreens.addAlertScreen.viewModel.ScreenState
import java.util.Calendar

@Composable
fun AddAlertScreen(navController: NavController) {

    val appContext = LocalContext.current.applicationContext

    val factory = remember {
        val appContainer = (appContext as MyApplication).appContainer
        val repo = appContainer.alertRepo
        AddAlertViewModelFactory(appContext, repo)
    }
    val viewModel = viewModel<AddAlertViewModel>(factory = factory)
//============================
    val alertState by viewModel.alertState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
//============================


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
    ) {

        when (screenState) {
            ScreenState.OnSelectedDateWrong -> {
                ShowWrongSnackbar(stringResource(R.string.start_time_must_be_after_time_right_now))
            }

            ScreenState.Ideal -> {}

            ScreenState.OnSelectedDateCorrect -> {
                navController.popBackStack()
            }

            ScreenState.OnUnSelectedDate -> {
                ShowWrongSnackbar(stringResource(R.string.unselected_date_check_again))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Top Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(end = 12.dp)
                )
                Text(
                    text = stringResource(R.string.new_alert),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Alert Type
            SectionLabel(stringResource(R.string.alert_type))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlertType.entries.forEach { type ->
                    val selected = alertState.alertType == type
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onAlertTypeChange(type) },
                        label = { Text(type.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White.copy(alpha = 0.3f),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            // Alert Value will hide if alert type is rain
            if (alertState.alertType != AlertType.RAIN) {
                SectionLabel("Threshold (${alertState.alertType.unit})")
                OutlinedTextField(
                    value = alertState.alertValue?.toString() ?: "",
                    onValueChange = { input ->
                        input.toDoubleOrNull()?.also { viewModel.onAlertValueChange(it) }
                    },
                    placeholder = {
                        Text(
                            "e.g. 40 (or more)",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Start Time
            SectionLabel(stringResource(R.string.start_time))
            TimePickerCard(
                defaultTime = alertState.startTime,
                onTimePicked = { viewModel.onStartTimeChange(it) }
            )

            // Alarm Kind
            SectionLabel(stringResource(R.string.alarm_type))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlarmKind.entries.forEach { kind ->
                    val selected = alertState.alarmKind == kind
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onAlarmKindChange(kind) },
                        label = { Text(kind.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White.copy(alpha = 0.3f),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveAlert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.25f)
                )
            ) {
                Text(
                    text = "Save Alert",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
fun ShowWrongSnackbar(message: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Snackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = message,
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }

}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )
}


@Preview(showBackground = false)
@Composable
fun Test() {
    TimePickerCard(defaultTime = 0, onTimePicked = {})
}

@Composable
fun TimePickerCard(
    defaultTime: Long,
    onTimePicked: (Long) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDateTimePicker(context, defaultTime, onTimePicked) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Text(
            text = defaultTime.toDateString(),
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            fontSize = 15.sp
        )
    }
}

fun showDateTimePicker(context: Context, currentTime: Long, onTimePicked: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onTimePicked(picked.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}