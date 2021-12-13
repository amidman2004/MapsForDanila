package com.example.kotlin16

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.kotlin16.ui.theme.Kotlin16Theme
import com.google.android.libraries.maps.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*



class MainActivity : ComponentActivity(){
    private lateinit var mMapView: MapView
    @SuppressLint("MissingPermission")
    val checkPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        mMapView.getMapAsync{googleMap->
            googleMap.isMyLocationEnabled = it
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin16Theme {
                Map()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun Map() {
        mMapView = MapLifeCycle()
        checkPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        AndroidView({mMapView}, modifier = Modifier.fillMaxSize()){mMapView ->
            CoroutineScope(Dispatchers.Main).launch{
                mMapView.getMapAsync {
                    it.uiSettings.isCompassEnabled = true
                    it.uiSettings.isZoomControlsEnabled = true
                    it.mapType = 1
                }
            }
        }
    }


    @Composable
    fun MapLifeCycle():MapView {
        val context = LocalContext.current
        val mapView = remember {
            MapView(context).apply {
                id = com.google.maps.android.ktx.R.id.map_frame
            }
        }
        val observer = Observer(mapView)
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle){
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
        return mapView
    }

    @Composable
    fun Observer(mapView: MapView):LifecycleEventObserver {
        return remember {
            LifecycleEventObserver { _, event ->
                when(event) {
                    Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> throw IllegalStateException()
                }
            }
        }
    }
}

