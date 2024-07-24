package com.example.batteryhealth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.batteryhealth.ui.theme.BatteryHealthTheme
import com.example.batteryhealth.utils.BATTERY_PROPERTY_MANUFACTURING_DATE
import com.example.batteryhealth.utils.BATTERY_PROPERTY_STATE_OF_HEALTH
import com.example.batteryhealth.utils.BATTERY_STATS_PERM
import com.example.batteryhealth.utils.EXTRA_CYCLE_COUNT
import com.example.batteryhealth.utils.getStatusResultString
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

class MainActivity : ComponentActivity() {
    var batteryLevelIntVal = mutableIntStateOf(20)
    var batteryLevelFloatVal = mutableFloatStateOf(0.1f)
    var batteryLevelStringVal = mutableStateOf("100%")
    var batteryTypeVal = mutableStateOf("-")
    var batteryTempVal = mutableStateOf("-")
    var batteryPowerSourceVal = mutableStateOf("Unknown")
    var batteryVoltageVal = mutableStateOf("-")
    var batteryStatusVal = mutableStateOf("-")
    var batteryHealthVal = mutableStateOf("-")
    var batteryFastChargingVal = mutableStateOf("-")
    var batteryCycleCount = mutableStateOf("-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryHealthTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding),
                    ) {
                        BatteryStatus()
                    }
                }
            }
        }

    }


    @Composable
    fun BatteryStatus() {
        val progress by remember { mutableFloatStateOf(0.1f) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            Image(
//                painter = painterResource(id = R.drawable.ic_health_good),
//                contentDescription = "Battery Status Icon"
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = "Healthy")
//            Text(text = "100% of original capacity", fontSize = 11.sp)
//            LinearProgressIndicator(
//                progress = { animatedProgress }
//            )
            SystemBroadcastReceiver(Intent.ACTION_BATTERY_CHANGED) { batteryStatus ->
                val batteryChargingStatus = batteryStatus?.getIntExtra("status", 0) ?: 0
                val batteryLevel = batteryStatus?.getIntExtra("level", 0) ?: 0
                batteryCycleCount.value =
                    batteryStatus?.getIntExtra(EXTRA_CYCLE_COUNT, -1).toString()
                batteryStatusVal.value = getStatusResultString(batteryChargingStatus)
                batteryLevelStringVal.value = "${batteryLevel}%"
                batteryLevelIntVal.intValue = batteryStatus?.getIntExtra("level", 0) ?: 0
                batteryLevelFloatVal.floatValue = batteryLevelIntVal.intValue.toFloat() / 100f
            }
            CardViewMain()



            if (Shell.getShell().isRoot) {
                Shell.cmd("pm grant com.example.batteryhealth android.permission.BATTERY_STATS")
                    .submit()
                // Get battery manager
                val mBatteryManager =
                    LocalContext.current.getSystemService(BatteryManager::class.java)
                // Get battery capacity
                val capacity = mBatteryManager.getIntProperty(BATTERY_PROPERTY_STATE_OF_HEALTH)
                Text(text = capacity.toString())
                // Get manufacturing date
                val manufacturingDate =
                    mBatteryManager.getLongProperty(BATTERY_PROPERTY_MANUFACTURING_DATE)
                Text(text = manufacturingDate.toString())
            }
        }
    }

    @Composable
    fun CardViewMain() {
        Column {
            Spacer(modifier = Modifier.padding(top = 6.dp))
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    //Battery Info Image logo composable function
                    ImageLogo(batteryLevelStringVal.value)
                    BatteryChargingIndicator(progress = batteryLevelFloatVal.floatValue)
                    Spacer(modifier = Modifier.height(2.dp))
                    TextHeader()
//                    OutlinedButton(
//                        onClick = {
//                            if (batteryLevelFloatVal.floatValue < 1f) {
//                                batteryLevelFloatVal.floatValue += 0.2f
//                            }
//                        }
//                    ) {
//                        Text("Increase")
//                    }

//                    RowComponentInCard("Battery Level", batteryLevelStringVal.value)
//                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Type", batteryTypeVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Temp", batteryTempVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Power Source", batteryPowerSourceVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Status", batteryStatusVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Voltage", batteryVoltageVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Battery Health", batteryHealthVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Fast Charging", batteryFastChargingVal.value)
                    HorizontalDivider(thickness = 0.5.dp)
                    RowComponentInCard("Cycle Count", batteryCycleCount.value)
                }//end of column
            }//end of card
        }//end of outer column
    }//end of card view main

    //Battery Image Logo composable function
    @Composable
    fun ImageLogo(valueOfBatteryTemp: String) {
        Box(
            modifier = Modifier.padding(5.dp),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = painterResource(R.drawable.battery),
                contentDescription = "Image Logo",
                modifier = Modifier
                    .requiredHeight(125.dp)
                    .requiredWidth(125.dp)
                    .padding(5.dp)
            )

            Text(
                text = valueOfBatteryTemp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .wrapContentHeight()
                    .wrapContentWidth(),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    @Composable
    fun BatteryChargingIndicator(progress: Float) {
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
        ).value
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF4caf50),
            progress = { animatedProgress },
            trackColor = Color(0xFF81c784)
        )
    }

    //Battery Info app name Text
    @Composable
    fun TextHeader() {
        Text(
            text = "BATTERY INFO",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge
        )
    }


    //Row component composable function for battery related info
    @Composable
    fun RowComponentInCard(strDesc: String, mutableVal: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = strDesc,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mutableVal,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .padding(5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    @Composable
    fun SystemBroadcastReceiver(
        systemAction: String,
        onSystemEvent: (intent: Intent?) -> Unit,
    ) {
        // Grab the current context in this part of the UI tree
        val context = LocalContext.current

        // Safely use the latest onSystemEvent lambda passed to the function
        val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

        // If either context or systemAction changes, unregister and register again
        DisposableEffect(context, systemAction) {
            val intentFilter = IntentFilter(systemAction)
            val broadcast = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    currentOnSystemEvent(intent)
                }
            }

            context.registerReceiver(broadcast, intentFilter)

            // When the effect leaves the Composition, remove the callback
            onDispose {
                context.unregisterReceiver(broadcast)
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MainActivityPreview() {
        BatteryHealthTheme {
            BatteryStatus()
        }
    }
}