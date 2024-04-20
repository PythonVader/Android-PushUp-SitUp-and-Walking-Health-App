package com.example.strongestmanalive.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.strongestmanalive.StrongestManAliveApplication
import com.example.strongestmanalive.sensors.MainSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.properties.Delegates

class MainViewModel(val accelerationSensor: MainSensor, val gyroSensor: MainSensor) : ViewModel() {

    private val _numberOfPushUps = MutableStateFlow(0)
    val numberOfPushUps = _numberOfPushUps.asStateFlow()

    private val _numberOfBurpees = MutableStateFlow(0)
    val numberOfBurpees = _numberOfBurpees.asStateFlow()


    private var _numberOfSteps = MutableStateFlow(0)
    val numberOfSteps = _numberOfSteps.asStateFlow()



    private var _initialDataArrayX by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
//       val a = if(newValue< -11.8) newValue + 9.7.toFloat()  else if (newValue>11.8) newValue - 9.7.toFloat() else 0F
        println("NEW VALUE X AXIS $newValue")
//        val x = newValue.subtract(oldValue.toSet())
//        println("New Value of x is $x")
//        val a = exponentialFilter(newValue,.45f)
//        val b = exponentialFilter(a,.45f)
//        val c = exponentialFilter(b,.45f)
//        val d = exponentialFilter(c,.45f)

//        _initialDataArrayXFiltered1 = exponentialFilter(a,.45f)
        _initialDataArrayXFiltered1 = newValue
    }
    private var _initialDataArrayXFiltered1 by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
//        Log.d(TAG,"New Value of_initialDataArrayXFiltered1: $newValue")
        _initialDataArrayXFiltered2 = movingAverageFilterFloat(newValue, 1)
    }
    private var _initialDataArrayXFiltered2 by Delegates.observable(0f){ property, oldValue, newValue ->
//        Log.d(TAG,"New Value of _initialDataArrayXFiltered2 $newValue")
        val newPhraseMadeIs = convertAverageToString(valueX = _initialDataArrayMFiltered2.value, valueY = newValue)
        phraseForPushUp= "$phraseForPushUp $newPhraseMadeIs"

    }
    private var _initialDataArrayM by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
//        Log.d(TAG,"New Value of _initialDataArrayM: $newValue")
//        val x = newValue.subtract(oldValue.toSet())
//        val a = exponentialFilter(newValue,.45f)
//        val b = exponentialFilter(a,45f)
//        val c = exponentialFilter(b,45f)
//        val d = exponentialFilter(c,45f)
//        val a = if(newValue< -11.8) newValue + 9.7.toFloat() else if (newValue>11.8) newValue - 9.7.toFloat() else 0F
        println("NEW VALUE Y AXIS $newValue")

//        _initialDataArrayMFiltered1 = exponentialFilter(a, 0.45f)
        _initialDataArrayMFiltered1 = newValue
    }
    private var _initialDataArrayMFiltered1 by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
//        Log.d(TAG,"New Value of _initialDataArrayMFiltered1 $newValue")
        val a = movingAverageFilterFloatY(newValue, 1)
        _initialDataArrayMFiltered2.value = a
    }
    private var _initialDataArrayMFiltered2 = mutableStateOf(0f)


    private var _initialDataArrayXBurpee by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
        println("NEW VALUE X AXIS Burpee --> $newValue")
        _initialDataArrayXFiltered1Burpee = newValue
    }
    private var _initialDataArrayXFiltered1Burpee by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
        _initialDataArrayXFiltered2Burpee = movingAverageFilterFloat(newValue, 1)
    }
    private var _initialDataArrayXFiltered2Burpee by Delegates.observable(0f){ property, oldValue, newValue ->
        val newPhraseMadeIs = convertAverageToString(valueX = _initialDataArrayMFiltered2Burpee.value, valueY = newValue)
        phraseForBurpees= "$phraseForBurpees $newPhraseMadeIs"

    }
    private var _initialDataArrayMBurpee by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
        println("NEW VALUE Y AXIS Burpee ---> $newValue")
        _initialDataArrayMFiltered1Burpee = newValue
    }
    private var _initialDataArrayMFiltered1Burpee by Delegates.observable(listOf(0f)){ property, oldValue, newValue ->
//        Log.d(TAG,"New Value of _initialDataArrayMFiltered1 $newValue")
        val a = movingAverageFilterFloatY(newValue, 1)
        _initialDataArrayMFiltered2Burpee.value = a
    }
    private var _initialDataArrayMFiltered2Burpee = mutableStateOf(0f)

    private var phraseForPushUp by Delegates.observable("") { _, _, newValue ->
        println("The PHRASE FOR PUSH UP COUNTED -------------> $newValue")
        if (checkForPushUp(newValue, "N  N  S  S")) {
            _numberOfPushUps.value = _numberOfPushUps.value.plus(1)
            resetPhraseForPushUp()
            println("Push Up Detected Resetting phrase")
        }
    }
    //    X0 X0 X0 X0 X0 SE  X0 X0 N  NE  N  S  S  SE  X0 NE  NE  NE  X0 S  S  SE  NE  NE  NE
//    SE  SE  X0 NE  NE  NE  S  S  SE  NE  NE  NE  NE  X0 S  SE  NE  NE  NE  X0 X0
    private var phraseForBurpees by Delegates.observable(""){ _,_,newValue ->
        println("The PHRASE FOR PUSH UP COUNTED -------------> $newValue")
        if(checkForSitUp(newValue, "NE SE")) {
            _numberOfBurpees.value = _numberOfBurpees.value.plus(1)
            resetPhraseForBurpees()
            println("Burpee Detected Resetting phrase ${numberOfBurpees.value}")
        }
    }
    private fun resetPhraseForPushUp(){
        phraseForPushUp = ""
    }
    private fun resetPhraseForBurpees(){
        phraseForBurpees = ""
    }
//    SE  N  NW  NW  NW  NW  NW  NW  NW  NW  NW  NW  NW
//    S NW NE NW NW NW NW NW NW
//    SW S W NW NW N N N N N N N NE NE
//    S NW N SE NW W SW SW SW SW SW NE NW
    //NW W N SE SE SE SE NE NE NE NE NE N SW SW SW

    fun startStepSensor(){
        gyroSensor.startListening()
        gyroSensor.setOnSensorValuesChangedListener { values ->
            _numberOfSteps.value = values[0].toInt()
            Log.d(TAG, "Steps since last reboot: ${_numberOfSteps.value}")

        }
    }

    fun stopStepSensor(){
        gyroSensor.stopListening()
    }

    fun startAccelSensor(){
        var listOfSensorReadingsX = arrayListOf<Float>()
        val listOfSensorReadingsY = arrayListOf<Float>()
        val listOfSensorReadingsZ = arrayListOf<Float>()
        var listOfSensorReadingsM = arrayListOf<Float>()

        accelerationSensor.startListening()
        accelerationSensor.setOnSensorValuesChangedListener { values ->
            val xAxisAcceleration = values[0]
            val yAxisAcceleration = values[1]
            val zAxisAcceleration = values[2]

            listOfSensorReadingsX.add(xAxisAcceleration)
            listOfSensorReadingsY.add(yAxisAcceleration)
            listOfSensorReadingsZ.add(zAxisAcceleration)
            listOfSensorReadingsM.add(sqrt(((yAxisAcceleration*yAxisAcceleration)+(zAxisAcceleration*zAxisAcceleration)).toDouble()).toFloat())

            println("$xAxisAcceleration")
            if (listOfSensorReadingsX.size >= 8){
//                println("ifAchieved and the value should be added")
//                println("The Value Of X access From the If is $listOfSensorReadingsX")
//                _initialDataArrayM = if(listOfSensorReadingsM.last() in -1.5f .. 1.5f) 0F else listOfSensorReadingsM.last()
//                _initialDataArrayX = if(listOfSensorReadingsX.last() in -1.5f .. 1.5f) 0F else listOfSensorReadingsX.last()
//                _initialDataArrayM = sqrt((values[1]*values[1])+(values[3]*values[3]))
//                _initialDataArrayX = values[0]
                _initialDataArrayX = listOfSensorReadingsX.filterNot { it in -1.5f..1.5f }
                _initialDataArrayM = listOfSensorReadingsM.filterNot { it in -5f..5f }
                println("Array Values are of X  = $listOfSensorReadingsM")
                println("Array Values are of MAG  = $listOfSensorReadingsX")
                listOfSensorReadingsX = arrayListOf()
                listOfSensorReadingsM = arrayListOf()

            }

        }
    }

    fun startAccelSensorSitup(){
        var listOfSensorReadingsX = arrayListOf<Float>()
        val listOfSensorReadingsY = arrayListOf<Float>()
        val listOfSensorReadingsZ = arrayListOf<Float>()
        var listOfSensorReadingsM = arrayListOf<Float>()

        accelerationSensor.startListening()
        accelerationSensor.setOnSensorValuesChangedListener { values ->
            val xAxisAcceleration = values[0]
            val yAxisAcceleration = values[1]
            val zAxisAcceleration = values[2]

            listOfSensorReadingsX.add(xAxisAcceleration)
            listOfSensorReadingsY.add(yAxisAcceleration)
            listOfSensorReadingsZ.add(zAxisAcceleration)
            listOfSensorReadingsM.add(sqrt(((yAxisAcceleration*yAxisAcceleration)+(zAxisAcceleration*zAxisAcceleration)).toDouble()).toFloat())
//            listOfSensorReadingsM.add(yAxisAcceleration)
            println("$xAxisAcceleration")
            if (listOfSensorReadingsX.size >= 8){
//                println("ifAchieved and the value should be added")
//                println("The Value Of X access From the If is $listOfSensorReadingsX")
//                _initialDataArrayM = if(listOfSensorReadingsM.last() in -1.5f .. 1.5f) 0F else listOfSensorReadingsM.last()
//                _initialDataArrayX = if(listOfSensorReadingsX.last() in -1.5f .. 1.5f) 0F else listOfSensorReadingsX.last()
//                _initialDataArrayM = sqrt((values[1]*values[1])+(values[3]*values[3]))
//                _initialDataArrayX = values[0]
                _initialDataArrayXBurpee = listOfSensorReadingsX.filterNot { it in -1.5f..1.5f }
                _initialDataArrayMBurpee = listOfSensorReadingsM.filterNot { it in -1.5f..1.5f }
                println("Array Values are of X Burpees = $listOfSensorReadingsM")
                println("Array Values are of MAG Burpees = $listOfSensorReadingsX")
                listOfSensorReadingsX = arrayListOf()
                listOfSensorReadingsM = arrayListOf()

            }

        }
    }

    private fun exponentialFilter(givenArray: List<Float>, alpha: Float): ArrayList<Float>{
        val returnArrayList: ArrayList<Float> = arrayListOf()
        val sizeOfArrayMinusOne = givenArray.size - 2
        for (i in 0..sizeOfArrayMinusOne){
            returnArrayList.add((alpha*givenArray[i])+(1-alpha)*givenArray[i+1])
        }
        return returnArrayList
    }

    private fun movingAverageFilter(givenArray: List<Float>, sample:Int) : ArrayList<Float>{
        val returnArray: ArrayList<Float> = arrayListOf()
        val sizeCountingForError = if (givenArray.size < 2) 2 else givenArray.size
        val listOfListMovedAverage = givenArray.windowed(sizeCountingForError, 1){
            it.average()
        }
        listOfListMovedAverage.forEach{
            println("Averaged to $it")
            returnArray.add(it.toFloat())
        }

        return returnArray
    }
    private fun movingAverageFilterFloat(givenArray: List<Float>, sample:Int) : Float {
        val returnArray: ArrayList<Float> = arrayListOf()
        val sizeCountingForError = if (givenArray.isEmpty()) 1 else givenArray.size
        val listOfListMovedAverage = givenArray.windowed(sizeCountingForError, 1){
            it.average()
        }
        if (listOfListMovedAverage.isNotEmpty()) println("Averaged of S#x ACCESS ACCELERATION to ${listOfListMovedAverage.last()}") else println("listisEmpty")
        return if (listOfListMovedAverage.isNotEmpty())listOfListMovedAverage.last().toFloat() else 100f
    }
    private fun movingAverageFilterFloatY(givenArray: List<Float>, sample:Int) : Float{
        val returnArray: ArrayList<Float> = arrayListOf()
        val sizeCountingForError = if (givenArray.size < 2) 2 else givenArray.size
        val listOfListMovedAverage = givenArray.windowed(sizeCountingForError, 1){
            it.average()
        }
        if (listOfListMovedAverage.isNotEmpty()) println("Averaged of Y ACCESS ACCELERATION to ${listOfListMovedAverage.last()}") else println("listisEmpty")

        return if (listOfListMovedAverage.isNotEmpty())listOfListMovedAverage.last().toFloat() else 0f
    }

    private fun convertToVectorString(xAxisArray: List<Float>, yAxisArray: List<Float>): String{
        var vector = ""
        for (i in 0..<(xAxisArray.size-1)){
            val deltaX = xAxisArray[i]-xAxisArray[i+1]
            val deltaY = yAxisArray[i]-yAxisArray[i+1]
            var theta = abs(atan2(xAxisArray[i],yAxisArray[i])*57.2958)
            if(theta<0){
                theta *= -1
                theta += 180
            }
            if((theta>=0 && theta<22.5)||(theta>=337.5&&theta<360)){
                vector += "N "
            }
            if(theta>=22.5&&theta<67.5){
                vector += "NE "
            }
            if(theta>=67.5&&theta<112.5){
                vector += "E "
            }
            if(theta>=112.5&&theta<157.5){
                vector += "SE "
            }
            if(theta>=157.5&&theta<202.5){
                vector += "S "
            }
            if(theta>=202.5&&theta<247.5){
                vector += "SW "
            }
            if(theta>=247.5&&theta<292.5){
                vector += "W "
            }
            if(theta>=292.5&&theta<337.5){
                vector += "NW "
            }
        }
        return vector
    }
    private fun convertAverageToString(valueX: Float, valueY: Float): String{
        var vector = ""
        var theta = atan2(valueX,valueY)*57.2958
        println("value in convertvector to string x is $valueX")

            if(valueY==100f) vector = "X0" else{
                if(theta<0){
                    theta *= -1
                    theta += 180
                }
                if((theta>=0 && theta<22.5)||(theta>=337.5&&theta<360)){
                    vector += "N "
                }
                if(theta>=22.5&&theta<67.5){
                    vector += "NE "
                }
                if(theta>=67.5&&theta<112.5){
                    vector += "E "
                }
                if(theta>=112.5&&theta<157.5){
                    vector += "SE "
                }
                if(theta>=157.5&&theta<202.5){
                    vector += "S "
                }
                if(theta>=202.5&&theta<247.5){
                    vector += "SW "
                }
                if(theta>=247.5&&theta<292.5){
                    vector += "W "
                }
                if(theta>=292.5&&theta<337.5){
                    vector += "NW "
                }
            }

        return vector
    }
    private fun checkForPushUp(input: String, phraseForPushUp: String): Boolean{
        return if (input.contains(phraseForPushUp) || input.contains("N  N  SE  S")){
            _numberOfPushUps.value.plus(1)
            true
        }else false
    }

    private fun checkForSitUp(input: String, phraseForPushUp: String): Boolean{
        return if (input.contains(phraseForPushUp) || input.contains("NE  NE  NE") || input.contains("NE  NE  X0 S  SE") || input.contains("S  SE  E  NE  NE") || input.contains("SE  N  NE  NE")) {
            _numberOfBurpees.value.plus(1)
            true
        }else false
    }
//    fun startAccelerationSensor(){
//        var numberOfUpsAndDowns = 0
//        var isPushUp by Delegates.observable(false){
//                property, oldValue, newValue ->
//            if (!oldValue && newValue){
//                numberOfUpsAndDowns = numberOfUpsAndDowns.plus(1)
//                _numberOfPushUps.value = numberOfUpsAndDowns/2
//            }
//        }
//        accelerationSensor.startListening()
//        accelerationSensor.setOnSensorValuesChangedListener { values ->
//
//            val xAxisAcceleration = values[1]
////            val yAxisAcceleration = values[1]
////            val zAxisAcceleration = values[2]
//            println("The X Acceleration Is $xAxisAcceleration")
////            if(xAxisAcceleration < -1){
////                println("Going Down")
////                isPushUp = false
////            }
////            if(xAxisAcceleration > 1){
////                println("Pushing UP")
////                isPushUp = true
////            }
//            isPushUp = when{
//                xAxisAcceleration<-1 -> {
//                    println("Going Down")
//                    false
//                }
//
//                xAxisAcceleration>2 -> {
//                    println("Pushing UP")
//                    true
//                }
//
//                else -> {println("SameState")
//                    false
//                }
//            }
//
//            println("The Number of pushups is ${numberOfUpsAndDowns / 2}")
////            println("The Y Acceleration Is $yAxisAcceleration")
////            println("The Z Acceleration Is $zAxisAcceleration")
//            _initialDataArray.value.
//        }
//
//
//    }
    fun stopAccelerationSensor(){
        accelerationSensor.stopListening()
        phraseForPushUp = ""
    }
    fun stopAccelerationSensorBurpee(){
        accelerationSensor.stopListening()
        phraseForPushUp = ""
    }
    fun stopGyroSensor(){
        gyroSensor.stopListening()
        phraseForPushUp = ""
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(

                    strongestManAliveApplication().container.accelerationSensor,
                    strongestManAliveApplication().container.gyroSensor
                )

            }
        }
    }
}

fun CreationExtras.strongestManAliveApplication(): StrongestManAliveApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StrongestManAliveApplication)
