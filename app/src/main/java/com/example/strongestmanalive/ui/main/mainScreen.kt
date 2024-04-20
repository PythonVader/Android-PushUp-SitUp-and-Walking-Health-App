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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.strongestmanalive.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

enum class Screens{
    MAIN, PUSHUP, SITUP, WALKING
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)) {
    var currentScreen by rememberSaveable {
        mutableStateOf(Screens.MAIN)
    }
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



    when(currentScreen){
        Screens.MAIN -> MainScreenM(onSitUpPress = { currentScreen = Screens.SITUP }, onPushUpPress = { currentScreen = Screens.PUSHUP }) {
            currentScreen = Screens.WALKING
        }
        Screens.PUSHUP -> PushUpScreen(
            numberOfPushUps = numberOfPushUps.value,
            startExercise = { mainViewModel.startAccelSensor() },
            stopExercise = { mainViewModel.stopAccelerationSensor() },
            onBackPress = {currentScreen = Screens.MAIN})
        Screens.SITUP -> SitUpScreen(
            numberOfSitUps = numberOfSitUps.value,
            startExercise = { mainViewModel.startAccelSensorBurpee() },
            stopExercise = { mainViewModel.stopAccelerationSensorBurpee() },
            onBackPress = {currentScreen = Screens.MAIN}
        )
        Screens.WALKING -> WalkingScreen(
            numberOfSteps = numberOfSteps.value,
            startExercise = { mainViewModel.startStepSensor() },
            stopExercise = { mainViewModel.stopGyrosSensor()},
            onBackPress = {currentScreen = Screens.MAIN}
        )
    }

}

@Composable
fun MainScreenM(onSitUpPress: () -> Unit, onPushUpPress: () -> Unit, onWalkingPress: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Text(text = "Start Your Favorite Exercise Now", Modifier.padding(horizontal = 4.dp, vertical = 16.dp))
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Button(modifier = Modifier.padding(4.dp), onClick = { onPushUpPress() }) {
                    Text(text = "PUSH-UPS")
                }
                Button(modifier = Modifier.padding(4.dp), onClick = { onSitUpPress() }) {
                    Text(text = "SIT-UPS")
                }
                Button(modifier = Modifier.padding(4.dp), onClick = { onWalkingPress() }) {
                    Text(text = "WALKING")
                }
            }
        }
    }
}

@Composable
fun PushUpScreen(numberOfPushUps: Int, startExercise: () -> Unit, stopExercise: () -> Unit, onBackPress: () -> Unit) {
    var isExerciseStarted by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), contentAlignment = Alignment.CenterStart){
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Text(text = "PUSH UP")
            }
            Image(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back ARROW", modifier = Modifier.clickable { onBackPress() })

        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = numberOfPushUps.toString(), fontSize = 42.sp)
            Spacer(modifier = Modifier.size(65.dp))
            Image(painter = if(!isExerciseStarted){painterResource(id = R.drawable.baseline_play_arrow_24)}else{
                painterResource(id = R.drawable.baseline_pause_24)}, contentDescription = "StartStop", modifier = Modifier
                .size(60.dp)
                .clickable {
                    isExerciseStarted = if (isExerciseStarted) {
                        stopExercise()
                        false
                    } else {
                        startExercise()
                        true
                    }
                })
        }
    }

}

@Composable
fun SitUpScreen(numberOfSitUps: Int, startExercise: () -> Unit, stopExercise: () -> Unit, onBackPress: () -> Unit) {
    var isExerciseStarted by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), contentAlignment = Alignment.CenterStart){
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Text(text = "SIT UP")
            }
            Image(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back ARROW", modifier = Modifier.clickable { onBackPress() })

        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = numberOfSitUps.toString(), fontSize = 42.sp)
            Spacer(modifier = Modifier.size(65.dp))
            Image(painter = if(!isExerciseStarted){painterResource(id = R.drawable.baseline_play_arrow_24)}else{
                painterResource(id = R.drawable.baseline_pause_24)}, contentDescription = "StartStop", modifier = Modifier
                .size(60.dp)
                .clickable {
                    isExerciseStarted = if (isExerciseStarted) {
                        stopExercise()
                        false
                    } else {
                        startExercise()
                        true
                    }
                })
        }
    }
}

@Composable
fun WalkingScreen(numberOfSteps: Int, startExercise: () -> Unit, stopExercise: () -> Unit, onBackPress: () -> Unit) {
    var isExerciseStarted by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), contentAlignment = Alignment.CenterStart){
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Text(text = "Walking Step Counter")
            }
            Image(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back ARROW", modifier = Modifier.clickable { onBackPress() })

        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = numberOfSteps.toString(), fontSize = 42.sp)
            Spacer(modifier = Modifier.size(65.dp))
            Image(painter = if(!isExerciseStarted){painterResource(id = R.drawable.baseline_play_arrow_24)}else{
                painterResource(id = R.drawable.baseline_pause_24)}, contentDescription = "StartStop", modifier = Modifier
                .size(60.dp)
                .clickable {
                    isExerciseStarted = if (isExerciseStarted) {
                        stopExercise()
                        false
                    } else {
                        startExercise()
                        true
                    }
                })
        }
    }
}

