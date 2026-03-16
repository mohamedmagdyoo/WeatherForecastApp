package com.example.weatherforecast.view.alertScreens.alertScreen.view

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.labs.R
import com.example.weatherforecast.MyApplication
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlertType
import com.example.weatherforecast.data.alert.model.toDateString
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.view.alertScreens.alertScreen.viewModel.AlertState
import com.example.weatherforecast.view.alertScreens.alertScreen.viewModel.AlertViewModel
import com.example.weatherforecast.view.alertScreens.alertScreen.viewModel.AlertViewModelFactory
import com.example.weatherforecast.view.mainActivity.Screens


@Composable
fun AlertScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext

    val factory = remember {
        val appContainer = (context as MyApplication).appContainer
        val alertRepo = appContainer.alertRepo
        AlertViewModelFactory(alertRepo)
    }
    val vm = viewModel<AlertViewModel>(factory = factory)

    //===============================
    val alertList by vm.alerts.collectAsStateWithLifecycle()
    val screenState by vm.screenState.collectAsStateWithLifecycle()
    //===============================

    val snackbarHostState = remember { SnackbarHostState() }
    //Used LaunchedEffect cause we have snackBar and nav (side effects)
    LaunchedEffect(screenState) {
        when (screenState) {
            AlertState.Ideal -> {}
            AlertState.OnDenied -> {
                snackbarHostState
                    .showSnackbar(context.getString(R.string.please_grant_notification_permission_to_use_this_feature))
                navController.navigate(Screens.HomeScreen)
            }

        }
    }

    //Ask for permission to send notification
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
            if (!isGranted) {
                vm.onDeniedPermeation()
            } else {
                vm.onSuccessPermeation()
            }
        }
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onClickToAddAlert(navController) }, // nav to add alert screen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add),
                )
            }
        }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
        ) {
            if (alertList.isEmpty()) {
                EmptyAlertsMessage(
                    modifier = Modifier.padding(padding)
                )
            } else {
                ShowAlertList(
                    modifier = Modifier.padding(padding),
                    alertList = alertList,
                    onDelete = { alert -> vm.deleteAlert(alert) },
                    onSwitch = { alert, isActive ->
                        vm.updateAlertActivation(
                            alert.id,
                            isActive
                        )
                    }
                )
            }
        }
    }

}

fun onClickToAddAlert(navController: NavController) {
    navController.navigate(Screens.AddAlertScreen)
}

@Composable
fun ShowAlertList(
    modifier: Modifier = Modifier,
    alertList: List<Alert>,
    onDelete: (Alert) -> Unit,
    onSwitch: (Alert, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(alertList, key = { it.id }) { alert ->
            AlertCard(
                alert = alert,
                onDelete = { onDelete(alert) },
                onSwitch = { isActive -> onSwitch(alert, isActive) }
            )
        }
    }
}

@Composable
fun AlertCard(
    alert: Alert,
    onDelete: () -> Unit,
    onSwitch: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_alarm),
                contentDescription = "Alarm",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                val alertType = when (alert.alertType) {
                    AlertType.TEMPERATURE -> stringResource(R.string.temperature)
                    AlertType.WIND -> stringResource(R.string.wind)
                    AlertType.RAIN -> stringResource(R.string.rain)
                }
                Text(
                    text = alertType,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (alert.alertType != AlertType.RAIN && alert.alertValue != null) {
                    Text(
                        text = stringResource(R.string.threshold, alert.alertValue),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = stringResource(
                        R.string.alert_time,
                        alert.startTime.toDateString(),
                        alert.startTime.toDateString()
                    ),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = alert.isActive,
                onCheckedChange = { onSwitch(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green.copy(alpha = 0.6f),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Alert",
                tint = Color.Red.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

@Composable
fun EmptyAlertsMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No alerts yet",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 18.sp
            )
            Text(
                text = "Tap + to add a weather alert",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        }
    }
}
