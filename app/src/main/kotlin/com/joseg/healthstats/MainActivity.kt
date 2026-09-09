package com.joseg.healthstats

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import com.joseg.healthstats.ui.HealthStatsNavHost

class MainActivity : ComponentActivity() {
    private val container get() = (application as HealthStatsApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var permissionResultTrigger by remember { mutableIntStateOf(0) }
                    val requestPermissionsLauncher = rememberLauncherForActivityResult(
                        contract = remember { container.healthConnectManager.requestPermissionsContract() },
                    ) {
                        permissionResultTrigger++
                    }

                    HealthStatsNavHost(
                        viewModelFactory = container.viewModelFactory,
                        onInstallOrUpdateHealthConnect = { openHealthConnectInPlayStore() },
                        onRequestPermissions = {
                            requestPermissionsLauncher.launch(HealthConnectManager.REQUIRED_PERMISSIONS)
                        },
                        permissionResultTrigger = permissionResultTrigger,
                    )
                }
            }
        }
    }

    private fun openHealthConnectInPlayStore() {
        // HealthConnectClient.DEFAULT_PROVIDER_PACKAGE_NAME is internal to the connect-client
        // module; this is the same package declared in the manifest's <queries> element.
        val providerPackage = "com.google.android.apps.healthdata"
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse("market://details?id=$providerPackage&url=healthconnect%3A%2F%2Fonboarding")
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                },
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$providerPackage"),
                ),
            )
        }
    }
}
