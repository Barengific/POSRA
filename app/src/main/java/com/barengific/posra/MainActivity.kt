package com.barengific.posra

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.barengific.posra.MainActivity.Companion.mediaPlayer
import com.barengific.posra.basket.Basket
import com.barengific.posra.basket.BasketAdapter
import com.barengific.posra.product.Product
import com.barengific.posra.product.ProductAdd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode

class MainActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView

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

    init {
        instance = this
    }

    //TODO
    //Order history list

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: MainActivityBinding

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNav.selectedItemId = R.id.nav_view

        supportActionBar?.hide();
        actionBar?.hide();

        binding.tvBarcodeMa.requestFocus();
        binding.root.hideKeyboard()
        binding.root.hideSoftInput()
//        binding.tvBarcodeMa.editText?.inputType = InputType.TYPE_NULL;

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

        if(Deets.arrBasket.size > 0 && Deets.arrBasket[0].name.toString() == ("0")){
            Deets.arrBasket.removeAt(0)
        }
        val adapter = Deets.arrBasket?.let { BasketAdapter(it) }
        recyclerView = findViewById<View>(R.id.rView) as RecyclerView
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val totalCost  = Deets.arrBasket.sumOf { it.total?.toDoubleOrNull()!! } //
        val totalQty  = Deets.arrBasket.sumOf { it.qty?.toIntOrNull()!! } //
        binding.tvTotalPrice.editText?.setText(totalCost.toString())
        binding.tvTotalQty.editText?.setText(totalQty.toString())

        bottomNav.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_home) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                false
            } else if (menuItem.itemId == R.id.nav_finish) {
                //
                false
            } else if (menuItem.itemId == R.id.nav_cancel) {
                //
                false
            } else if (menuItem.itemId == R.id.nav_manual) {
                val newDialogFragment = StartersDialogFragment()
                val transaction: FragmentTransaction =
                    this.supportFragmentManager.beginTransaction()
                newDialogFragment.show(transaction, "New_Dialog_Fragment")
                true
            }else {
                false
            }
        }
        binding.root.hideKeyboard()

        binding.tvBarcodeMa.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(binding.tvBarcodeMa.editText?.text.toString().contains("\n")){
                    val barcodeS = binding.tvBarcodeMa.editText?.text.toString()
                    val barcodeStr = barcodeS.dropLast(1)

                    try{
                        val itemA: Product = productDAO.findByBarcodeExact(barcodeStr)
                        val itemTotal = itemA.price.toString().toInt() * 1
                        val findSame : Basket? = Deets.arrBasket.firstOrNull() { it.barcode == barcodeStr }
                        Log.d("aaaaafindSame",findSame.toString())

                        if(findSame == null || findSame?.barcode.toString() != barcodeStr){
                            Log.d("aaaaa","not found sameeeee")
                            Deets.arrBasket?.add(Basket(0, itemA.name, itemA.price,
                                    "1", itemTotal.toString(), itemA.barcode.toString()))
                        }else{
                            Log.d("aaaaa","found!!! sameeeee")
                            val findSameIndex = Deets.arrBasket.indexOf(findSame)
                            val findSameQty = (Deets.arrBasket.get(findSameIndex).qty?.toInt()
                                ?.plus(1)).toString()

                            Deets.arrBasket.set(findSameIndex, Basket(Deets.arrBasket.get(findSameIndex).id,
                                Deets.arrBasket.get(findSameIndex).name,
                                Deets.arrBasket.get(findSameIndex).price,
                                findSameQty,
                                (Deets.arrBasket.get(findSameIndex).price?.toDouble()
                                    ?.times(findSameQty.toDouble())).toString(),
                                Deets.arrBasket.get(findSameIndex).barcode))

                        }
                    }catch(ex: Exception){
                        Log.d("aaaaaaQWERT", ex.toString())
                    }
                    binding.tvBarcodeMa.editText?.text?.clear()
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }


                }
            }
        })

    }

    fun View.hideKeyboard() {
        val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    fun View.hideSoftInput() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
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
                            mediaPlayer?.setOnPreparedListener{}
                            mediaPlayer?.start()

                            //db initialise
                            val passphrase: ByteArray =
                                net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
                            val factory = SupportFactory(passphrase)
                            val room =
                                Room.databaseBuilder(context, AppDatabase::class.java, "database-names")
                                    .openHelperFactory(factory)
                                    .allowMainThreadQueries()
                                    .build()
                            val productDAO = room.productDao()

                            val itemA: Product = productDAO.findByBarcodeExact(barcodeValue)
                            val itemTotal = itemA.price.toString().toInt() * 1

                            //TODO
                            //left-hand side item number should increment
                            //find duplicate item in basket and increment qty and multiple total
                            //
                            //option for finishing with the order
                            //option for cancel order
                            val findSame : Basket? = Deets.arrBasket.firstOrNull() { it.barcode == barcode.rawValue }
                            Log.d("aaaaa",findSame.toString())
                            if(findSame == null || findSame?.barcode.toString() != barcode.rawValue){
                                Log.d("aaaaa","not found sameeeee")
                                Deets.arrBasket?.add(Basket(0,itemA.name, itemA.price, "1", itemTotal.toString(),itemA.barcode.toString()))
                            }else{
                                val findSameIndex = Deets.arrBasket.indexOf(findSame)
                                val findSameQty = (Deets.arrBasket.get(findSameIndex).qty?.toInt()
                                    ?.plus(1)).toString()

                                Deets.arrBasket.set(findSameIndex, Basket(Deets.arrBasket.get(findSameIndex).id,
                                    Deets.arrBasket.get(findSameIndex).name,
                                    Deets.arrBasket.get(findSameIndex).price,
                                    findSameQty,
                                    (Deets.arrBasket.get(findSameIndex).price?.toDouble()
                                        ?.times(findSameQty.toDouble())).toString(),
                                    Deets.arrBasket.get(findSameIndex).barcode))
                                Log.d("aaaaa","found!!! sameeeee")
                            }
//                            if(findSame != null && findSame.barcode.toString() != barcode.rawValue){
//                                Log.d("aaaaa","not found sameeeee")
//                                Deets.arrBasket?.add(Basket(0,itemA.name, itemA.price, "1", itemTotal.toString(),itemA.barcode.toString()))
//                            }


//                            Deets.arrBasket.find { barcoder -> barcode.equals(barcoder) }
//                            Deets.arrBasket.first { it.barcode == barcode.rawValue }
//
//
//                            Log.d("aaaaa",
//                                Deets.arrBasket.first { it.barcode == barcode.rawValue }.toString()
//                            )

//                            val theFirstBatman = batmans.find { actor -> "Michael Keaton".equals(actor) }
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

class StartersDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            builder.setView(inflater.inflate(R.layout.manual_barcode_layout, null))
                .setPositiveButton("Confirmsss"
                ) { _, _ ->
                    val context: Context = MainActivity.applicationContext()
                    val dialogView = inflater.inflate(R.layout.manual_barcode_layout, null)
                    val barcode = dialogView.findViewById<EditText>(R.id.dl_barcode)
                    val barcodeStr = barcode.text.toString()

                    //db initialise
                    val passphrase: ByteArray =
                        net.sqlcipher.database.SQLiteDatabase.getBytes("bob".toCharArray())
                    val factory = SupportFactory(passphrase)
                    val room =
                        Room.databaseBuilder(context, AppDatabase::class.java, "database-names")
                            .openHelperFactory(factory)
                            .allowMainThreadQueries()
                            .build()
                    val productDAO = room.productDao()

                    val itemA: Product = productDAO.findByBarcodeExact(barcodeStr)
                    val itemTotal = itemA.price.toString().toInt() * 1

                    //TODO
                    //left-hand side item number should increment
                    //find duplicate item in basket and increment qty and multiple total
                    //
                    //option for finishing with the order
                    //option for cancel order
                    val findSame : Basket? = Deets.arrBasket.firstOrNull() { it.barcode == barcodeStr }
                    Log.d("aaaaa",findSame.toString())
                    if(findSame == null || findSame?.barcode.toString() != barcodeStr){
                        Log.d("aaaaa","not found sameeeee")
                        Deets.arrBasket?.add(Basket(0,itemA.name, itemA.price, "1", itemTotal.toString(),itemA.barcode.toString()))
                    }else{
                        val findSameIndex = Deets.arrBasket.indexOf(findSame)
                        val findSameQty = (Deets.arrBasket.get(findSameIndex).qty?.toInt()
                            ?.plus(1)).toString()

                        Deets.arrBasket.set(findSameIndex, Basket(Deets.arrBasket.get(findSameIndex).id,
                            Deets.arrBasket.get(findSameIndex).name,
                            Deets.arrBasket.get(findSameIndex).price,
                            findSameQty,
                            (Deets.arrBasket.get(findSameIndex).price?.toDouble()
                                ?.times(findSameQty.toDouble())).toString(),
                            Deets.arrBasket.get(findSameIndex).barcode))
                        Log.d("aaaaa","found!!! sameeeee")
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
