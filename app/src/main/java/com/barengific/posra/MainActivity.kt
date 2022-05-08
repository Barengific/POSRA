package com.barengific.posra

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.barengific.posra.MainActivity.Companion.baa
import com.barengific.posra.MainActivity.Companion.myList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var latestBCode: String = ""

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    companion object {
        var baa: String = ""
        var myList : List<String> = mutableListOf("")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        val list = listOf("A")
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(items = list, itemContent = { item ->
                                Text(text = item, style = TextStyle(fontSize = 10.sp))
                                CameraPreview()
                                MySimpleListItem()
                            })

                        }

                        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

//                        LazyColumnDemo()
//                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        ) {
                            Text(text = "Camera Permission")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                home()
                            }
                        ) {
                            Text(text = "Home")
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        CameraPreview()
                    }
                }

        }
    }
    private fun home(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }

    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        update = { previewView ->
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal.value = barcodeValue
                            Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()
                            latestBCode = barcodeValue
                            baa = barcodeValue
                            myList = mutableListOf(barcodeValue)
                            //
                            //
                            //

                        }
                    }
                }
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

data class Message(val author: String, val body: String)
data class ItemViewState(
    val text: String
)

//@Composable
//fun MyComposeList(
//    modifier: Modifier = Modifier,
//    itemViewStates: List<ItemViewState>
//) {
//
//    // Use LazyRow when making horizontal lists
//    LazyColumn(modifier = modifier) {
//        items(ItemViewState) { data ->
//            MySimpleListItem()
//        }
//    }
//}
// The UI for each list item can be generated by a reusable composable
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MySimpleListItem() {
    Text(baa)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleListItem() {
    Text(baa)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LazyColumnDemo() {
//    val list = listOf("A", "B", "C", "D") + ((0..100).map { it.toString() })
    val list = listOf("A", "B", "C", "D", baa)
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items = list, itemContent = { item ->
            Log.d("COMPOSE", "This get rendered $item")
            when (item) {
                "A" -> {
                    Text(text = item, style = TextStyle(fontSize = 10.sp))
                }
                "B" -> {
                    Button(onClick = {}) {
                        Text(text = item, style = TextStyle(fontSize = 10.sp))
                    }
                }
//                "C" -> {
//                    Text(text = item, style = TextStyle(fontSize = 10.sp))
//                }
//                "D" -> {
//                    Text(text = item)
//                }
//                else -> {
//                    Text(text = item, style = TextStyle(fontSize = 10.sp))
//                }
            }
        })
    }
}