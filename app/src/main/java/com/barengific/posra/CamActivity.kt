package com.barengific.posra

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.barengific.posra.AddProduct.Companion.applicationContext
import com.barengific.posra.databinding.CamActivityBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamActivity : AppCompatActivity(){

    companion object{
        private var instance: CamActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }


//    object Utilser {
//        var context: Context? = CamActivity.applicationContext()
//            get() {
//                return CamActivity.applicationContext()
//            }
//            set(value) {
//                field = value?.applicationContext
//            }
//    }

    init {
        CamActivity.instance = this
    }

    private lateinit var binding: CamActivityBinding
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CamActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewViewCam.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
}

class BarcodeAnalyzer : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13,Barcode.FORMAT_EAN_8,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E)
                .build()
            val scanner = BarcodeScanning.getClient(options)

//            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        // Handle received barcodes...
                        val context: Context = CamActivity.applicationContext()

                        Toast.makeText(
                            context,
                            "Value: " + barcode.rawValue,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        barcode.rawValue?.let { barcodeValue ->
                            MainActivity.mediaPlayer = MediaPlayer.create(context, R.raw.scanner)
                            MainActivity.mediaPlayer?.setOnPreparedListener{
                                println("aa")
                            }
                            MainActivity.mediaPlayer?.start()
                            Deets.arrr.add(Basket(0,"2", barcodeValue, "222", "2222"))
                            Deets.bc_value = barcodeValue
//                            val value: String = barcodeValue // or just your string
                            val intent = Intent(context, AddProduct::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("barcodeScanned", value)
                            context.startActivity(intent)

                        }
                    }
                }
                .addOnFailureListener { }
        }

        image.close()
    }
}