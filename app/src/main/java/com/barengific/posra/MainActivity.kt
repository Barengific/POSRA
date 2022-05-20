package com.barengific.posra

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.barengific.posra.databinding.MainActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import net.sqlcipher.database.SupportFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.media.MediaPlayer
import com.barengific.posra.MainActivity.Companion.mediaPlayer
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode

class MainActivity : AppCompatActivity() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
        var room: Room? = null
        lateinit var recyclerView: RecyclerView
        var posis: MutableList<Int> = mutableListOf(-1)
        fun getPosi(): Int = pos
        fun setPosi(pos: Int) {
            this.pos = pos
        }

        private var instance: MainActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    object Utilsz {
        var context: Context? = applicationContext()
            get() {
                return applicationContext()
            }
            set(value) {
                field = value?.applicationContext
            }
    }

    init {
        instance = this
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        supportActionBar?.hide();
        actionBar?.hide();

        //db initialise
        val passphrase: ByteArray =
            net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        room =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")?
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
        val productDAO = room.productDao()
        val staffDAO = room.staffDao()
//        val staffDAO = room.staffDao()
//        val staffDAO = room.staffDao()


        //recycle view
//        val arr = productDAO.getAll()
        if(Deets.arrBasket[0].name.toString() == ("0")){
            Deets.arrBasket.removeAt(0)
        }
        val adapter = Deets.arrBasket?.let { BasketAdapter(it) }
        recyclerView = findViewById<View>(R.id.rView) as RecyclerView
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun checkCameraPermission() {
        try {
            val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, requiredPermissions, 0)
        } catch (e: IllegalArgumentException) {
            checkIfCameraPermissionIsGranted()
        }
    }

    private fun checkIfCameraPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted: start the preview
            startCamera()
        } else {
            // Permission denied
            MaterialAlertDialogBuilder(this)
                .setTitle("Permission required")
                .setMessage("This application needs to access the camera to process barcodes")
                .setPositiveButton("Ok") { _, _ ->
                    // Keep asking for permission until granted
                    checkCameraPermission()
                }
                .setCancelable(false)
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                    show()
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkIfCameraPermissionIsGranted()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer())
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

    @OptIn(ExperimentalPermissionsApi::class)
    private fun home() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}


class QrCodeAnalyzer : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        // Handle received barcodes...
                        val context: Context = MainActivity.applicationContext()

                        Toast.makeText(context, "Value: " + barcode.rawValue,
                            Toast.LENGTH_SHORT
                        ).show()

                        barcode.rawValue?.let { barcodeValue ->
                            mediaPlayer = MediaPlayer.create(context, R.raw.scanner)
                            mediaPlayer?.setOnPreparedListener{
                                println("aa")
                            }
                            mediaPlayer?.start()

                            Deets.arrBasket?.add(Basket(0,"2", barcodeValue, "222", "2222"))
                            val intent = Intent(context, PasserActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent)

                        }
                    }
                }
                .addOnFailureListener { }
        }

        image.close()
    }
}
