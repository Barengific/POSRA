package com.barengific.posra

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barengific.posra.MainActivity.Companion.a
import com.barengific.posra.MainActivity.Companion.baa
import com.barengific.posra.MainActivity.Companion.myList
import com.barengific.posra.databinding.MainActivityBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        var baa: String = ""
        var myList : List<String> = mutableListOf("")
        fun a(aa: String) : String = aa
        lateinit var recyclerView: RecyclerView

        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    fun a(aa: String){
        Toast.makeText(this, aa, Toast.LENGTH_SHORT).show()
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val context: Context = MainActivity.applicationContext()

        cameraExecutor = Executors.newSingleThreadExecutor()


        startCamera()

        //recycle view
        val arr = wordDao.getAll()
        val adapter = CustomAdapter(arr)
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
    private fun home(){
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

            // Process image searching for barcodes
//            val options = BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//                .build()

//            val scanner = BarcodeScanning.getClient(options)
            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        // Handle received barcodes...
                        val context: Context = MainActivity.applicationContext()

                        Toast.makeText(context,
                            "Value: " + barcode.rawValue,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        barcode.rawValue?.let { barcodeValue ->
                            baa = barcodeValue
                            a(baa)
                        }
                        //TOAST
                        a(barcode.toString())
                    }
                }
                .addOnFailureListener { }
        }

        image.close()
    }
}


class CustomAdapter(private val dataSet: List<Word>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {
        var ivCopy: ImageView
        var ivMore: ImageView

        @SuppressLint("ResourceType")
        override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            MainActivity.pos = adapterPosition
            MainActivity.setPosi(layoutPosition)
        }

        val textView1: TextView
        val textView2: TextView
        val textView3: TextView
        val textView4: TextView

        init {
            ivCopy = view.findViewById(R.id.ivCopy) as ImageView
            ivMore = view.findViewById(R.id.ivMore) as ImageView
            view.setOnCreateContextMenuListener(this)

            // Define click listener for the ViewHolder's View.
            textView1 = view.findViewById(R.id.textView1)
            textView2 = view.findViewById(R.id.textView2)
            textView3 = view.findViewById(R.id.textView3)
            textView4 = view.findViewById(R.id.textView4)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        viewHolder.itemView.setOnLongClickListener {
            setPosition(viewHolder.layoutPosition)
            setPosition(viewHolder.adapterPosition)
            false
        }

        viewHolder.ivMore.setOnClickListener { view ->
            val wrapper: Context = ContextThemeWrapper(view?.context, R.style.PopupMenu)

            val popup = PopupMenu(wrapper, viewHolder.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.rv_menu_context)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_copy -> {
//                        Log.d("aaa menu", "copy")
                        val clipboard =
                            view?.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData =
                            ClipData.newPlainText("PGen", viewHolder.textView4.text.toString())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(view.context, "Text Copied", Toast.LENGTH_LONG).show()

                    }
                    R.id.menu_delete -> {
                        val passphrase: ByteArray = SQLiteDatabase.getBytes("bob".toCharArray())
                        val factory = SupportFactory(passphrase)
                        val room = view?.context?.let {
                            Room.databaseBuilder(it, AppDatabase::class.java, "database-names")
                                .openHelperFactory(factory)
                                .allowMainThreadQueries()
                                .build()
                        }
                        val wordDao = room?.wordDao()

                        val wid: TextView = viewHolder.textView1
                        val pType: TextView = viewHolder.textView2
                        val key: TextView = viewHolder.textView3
                        val value: TextView = viewHolder.textView4

                        val a = Word(
                            wid.text.toString().toInt(),
                            pType.text.toString(),
                            key.text.toString(),
                            value.text.toString()
                        )
                        room?.wordDao()?.delete(a)
                        val arrr = wordDao?.getAll()
                        val adapter = arrr?.let { CustomAdapter(it) }

                        MainActivity.recyclerView.setHasFixedSize(false)
                        MainActivity.recyclerView.adapter = adapter
                        MainActivity.recyclerView.layoutManager =
                            LinearLayoutManager(view?.context)
                        room?.close()
//                        Log.d("aaa menu", "DDDelete")

                    }

                    R.id.menu_cancel -> {
//                        Log.d("aaa menu", "cancel")
                    }
                    R.id.menu_hide -> {
                        val passphrase: ByteArray =
                            SQLiteDatabase.getBytes("bob".toCharArray())//DB passphrase change
                        val factory = SupportFactory(passphrase)
                        val room = view?.context?.let {
                            Room.databaseBuilder(it, AppDatabase::class.java, "database-names")
                                .openHelperFactory(factory)
                                .allowMainThreadQueries()
                                .build()
                        }
                        val wordDao = room?.wordDao()

                        val arrr = wordDao?.getAll()

                        if (MainActivity.posis.contains(viewHolder.adapterPosition)) {//if existent then show
                            MainActivity.posis.remove(viewHolder.adapterPosition)

                            val pSize = MainActivity.posis.size
                            for (i in 0 until pSize) {
//                                Log.d("aaaaCVCVCVQQ", MainActivity.posis[i].toString())
                                if ((MainActivity.posis[i] != -1)) {
                                    val qSize = MainActivity.posis[i]
                                    arrr?.get(qSize)?.value = "****"
                                    arrr?.get(qSize)?.key = "****"
                                }
                            }

                            val adapter = arrr?.let { CustomAdapter(it) }
                            MainActivity.recyclerView.setHasFixedSize(false)
                            MainActivity.recyclerView.adapter = adapter
                            MainActivity.recyclerView.layoutManager =
                                LinearLayoutManager(view?.context)
                            room?.close()

                        } else {//if not existent then hide
                            MainActivity.posis.add(viewHolder.adapterPosition)

                            val pSize = MainActivity.posis.size
                            for (i in 0 until pSize) {
//                                Log.d("aaaaCVCVCV", MainActivity.posis[i].toString())
                                if ((MainActivity.posis[i] != -1)) {
                                    val qSize = MainActivity.posis[i]
                                    arrr?.get(qSize)?.value = "****"
                                    arrr?.get(qSize)?.key = "****"
                                }
                            }
                            val adapter = arrr?.let { CustomAdapter(it) }
                            MainActivity.recyclerView.setHasFixedSize(false)
                            MainActivity.recyclerView.adapter = adapter
                            MainActivity.recyclerView.layoutManager =
                                LinearLayoutManager(view?.context)
                            room?.close()

                        }

                    }

                }
                true
            }
            //displaying the popup
            popup.show()
        }

        viewHolder.ivCopy.setOnClickListener { view ->
//            Log.d("aaaaICONu", "inn copy")
            val clipboard = view?.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("PGen", viewHolder.textView4.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(view.context, "Text Copied", Toast.LENGTH_LONG).show()
        }

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView1.text = dataSet[position].wid.toString()
        viewHolder.textView2.text = dataSet[position].pType.toString()
        viewHolder.textView3.text = dataSet[position].key.toString()
        viewHolder.textView4.text = dataSet[position].value.toString()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = dataSet.size

    private var position: Int = 0

//    fun getPosition(): Int {
//        return position
//    }

    private fun setPosition(position: Int) {
        this.position = position
    }

}