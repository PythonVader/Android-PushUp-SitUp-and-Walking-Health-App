package com.example.strongestmanalive.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.example.strongestmanalive.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)) {
    val numberOfPushUps = mainViewModel.numberOfPushUps.collectAsState()
    val numberOfSitUps = mainViewModel.numberOfBurpees.collectAsState()
    val numberOfSteps = mainViewModel.numberOfSteps.collectAsState()

    val stepCounterPermissionState = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
            Log.d("MAIN", "NO Permission Granted")
        }
    }

    LaunchedEffect(stepCounterPermissionState) {
        if (!stepCounterPermissionState.status.isGranted && stepCounterPermissionState.status.shouldShowRationale) {
            // Show rationale if needed
            Log.d("MAIN", "SHOULD SHOW RATIONAL")
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }
    var isStarted by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = numberOfSitUps.value.toString(), fontSize = 42.sp)
        Spacer(modifier = Modifier.size(65.dp))
        Image(painter = if(!isStarted){painterResource(id = R.drawable.baseline_play_arrow_24)}else{
            painterResource(id = R.drawable.baseline_pause_24)}, contentDescription = "StartStop", modifier = Modifier
            .size(60.dp)
            .clickable {
                isStarted = if (isStarted) {
                    mainViewModel.stopAccelerationSensorBurpee()
                    false
                } else {
                    mainViewModel.startAccelSensorBurpee()
                    true
                }
            })
    }
}

