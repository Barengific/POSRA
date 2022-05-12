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

class MainActivity : AppCompatActivity() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
        var pos: Int = 0
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

        //db initialise
        val passphrase: ByteArray =
            net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
        val factory = SupportFactory(passphrase)
        val room =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-names")
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
        val productDAO = room.productDao()
        val staffDAO = room.staffDao()

        //recycle view
//        val arr = productDAO.getAll()
        val adapter = CustomAdapter(Deets.arrr)
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

//            Process image searching for barcodes
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

                        Toast.makeText(
                            context,
                            "Value: " + barcode.rawValue,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        barcode.rawValue?.let { barcodeValue ->
                            mediaPlayer = MediaPlayer.create(context, R.raw.scanner)
                            mediaPlayer?.setOnPreparedListener{
                                println("aa")
                            }
                            mediaPlayer?.start()
                            Deets.arrr.add(Basket(0,"2", barcodeValue, "222", "2222"))
                            val value: String = barcodeValue // or just your string
                            val intent = Intent(context, PasserActivity::class.java)
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


class CustomAdapter(private val dataSet: MutableList<Basket>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {
        var ivMore: ImageView

        @SuppressLint("ResourceType")
        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            MainActivity.pos = adapterPosition
            MainActivity.setPosi(layoutPosition)
        }

        val tv_id: TextView
        val tv_name: TextView
        val tv_price: TextView
        val tv_qty: TextView
        val tv_total: TextView

        init {
            ivMore = view.findViewById(R.id.ivMore) as ImageView
            view.setOnCreateContextMenuListener(this)

            // Define click listener for the ViewHolder's View.
            tv_id = view.findViewById(R.id.tv_id)
            tv_name = view.findViewById(R.id.tv_name)
            tv_price = view.findViewById(R.id.tv_price)
            tv_qty = view.findViewById(R.id.tv_qty)
            tv_total = view.findViewById(R.id.tv_total)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rv_basket, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        viewHolder.itemView.setOnLongClickListener {
            setPosition(viewHolder.layoutPosition)
            setPosition(viewHolder.adapterPosition)
            false
        }

        viewHolder.ivMore.setOnClickListener { view ->
            val wrapper: Context = ContextThemeWrapper(view?.context, R.style.PopupMenu)

            val popup = PopupMenu(wrapper, viewHolder.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.rv_basket_context_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        val passphrase: ByteArray =
                            net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
                        val factory = SupportFactory(passphrase)
                        val room = view?.context?.let {
                            Room.databaseBuilder(it, AppDatabase::class.java, "database-names")
                                .openHelperFactory(factory)
                                .allowMainThreadQueries()
                                .build()
                        }
                        val productDao = room?.productDao()

                        val id: TextView = viewHolder.tv_id
                        val name: TextView = viewHolder.tv_name
                        val price: TextView = viewHolder.tv_price
                        val qty: TextView = viewHolder.tv_qty
                        val total: TextView = viewHolder.tv_total

                        val a = Basket(
                            id.text.toString().toInt(),
                            name.text.toString(),
                            price.text.toString(),
                            qty.text.toString(),
                            total.text.toString()
                        )
                        Deets.arrr.remove(a)
                        val arrr = Deets.arrr

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

                }
                true
            }
            //displaying the popup
            popup.show()
        }

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tv_id.text = dataSet[position].id.toString()
        viewHolder.tv_name.text = dataSet[position].name.toString()
        viewHolder.tv_price.text = dataSet[position].price.toString()
        viewHolder.tv_qty.text = dataSet[position].qty.toString()
        viewHolder.tv_total.text = dataSet[position].total.toString()
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