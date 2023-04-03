package com.example.turbo.generatebarcode.components.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.turbo.R
import com.example.turbo.databinding.FragmentGenerateBarcodeBinding
import com.example.turbo.generatebarcode.components.StateListener
import com.example.turbo.generatebarcode.components.presentation.model.ItemModel
import com.example.turbo.printPdfFeature.PDFUtils
import com.example.turbo.printPdfFeature.PdfDocumentAdapter
import com.example.turbo.utils.ViewUtils
import com.example.turbo.utils.ViewUtils.show
import com.example.turbo.utils.ViewUtils.toast
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.glxn.qrgen.android.QRCode
import java.io.*

@AndroidEntryPoint
class GenerateBarcodeFragment : Fragment(),StateListener{
    private val viewModel by viewModels<GenerateBarcodeViewModel>()

    companion object {
        fun newInstance() = GenerateBarcodeFragment()

    }
    val model=ItemModel()
    private var barcodeNumber: String=""
    private lateinit var binding: FragmentGenerateBarcodeBinding
    private lateinit var valueEd: String
    var itemModelList=ArrayList<ItemModel>()

internal var printing:Printing?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_generate_barcode, container, false
            )

        /* check if printer has been paired or initialize pairing */
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()

        return binding.root
    }

    private fun initListeners() {
        binding.buttonPrint.setOnClickListener {
            askForPermissions()

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel=viewModel
        viewModel.stateListener=this

        initListeners()


    }


    private fun initModel(bitmap: Bitmap, barcodeNumber: String) {

        model.itemScannedBarcode= barcodeNumber
        model.itemImageBarcodeBitmap= bitmap
        model.itemStore="ياندا ستور"
        model.itemCity="القاهره"
        model.itemUOM="شبرا الخيمة"
        model.itemScannedQR_Code="01128717755"
        itemModelList.add(model)

    }



    private fun displayBitmap(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)
        val bitmap = createBarcodeBitmap(
            barcodeValue = value,
            barcodeColor = resources.getColor(android.R.color.holo_blue_bright),
            backgroundColor = resources.getColor(android.R.color.white),
            widthPixels = widthPixels,
            heightPixels = heightPixels
        )

        binding.imageBarcode.setImageBitmap(bitmap)
        barcodeNumber=binding.editTextBarcodeNumber.getText().toString()
        if (barcodeNumber.isNotEmpty()) {
            binding.textViewBarcodeNumber.setText(barcodeNumber)
            binding.editTextBarcodeNumber.visibility = View.GONE
            binding.buttonGenerateBarcode.visibility = View.GONE
            binding.buttonPrint.visibility = View.VISIBLE



        }
        initModel(bitmap,barcodeNumber)
    }

    private fun initView() {

        if (!Printooth.hasPairedPrinter()) {
            resultLauncher.launch(
                Intent(
                    context,
                    ScanningActivity::class.java
                ),
            )
        }
        else{
            printDetails()
        }



    /* callback from printooth to get printer process */
    printing?.printingCallback = object : PrintingCallback {
        override fun connectingWithPrinter() {
            toast("Connecting with printer",context)
        }

        override fun printingOrderSentSuccessfully() {
            toast("Order sent to printer",context)
        }

        override fun connectionFailed(error: String) {
            toast("Failed to connect printer",context)
        }

        override fun onError(error: String) {
            toast(error,context)
        }

        override fun onMessage(message: String) {
            toast("Message: $message",context)
        }

        override fun disconnected() {
            toast("Disconnected Printer",context)

        }

    }
}

private fun printDetails() {
    val printables = getSomePrintables()
    printing?.print(printables)
}

/* Customize your printer here with text, logo and QR code */
private fun getSomePrintables() = ArrayList<Printable>().apply {

    add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode


    //logo
//            add(ImagePrintable.Builder(R.drawable.bold, resources)
//                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//                    .build())

    add(
        ImagePrintable.Builder(model.itemImageBarcodeBitmap)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .build())

    add(
        TextPrintable.Builder()
            .setText(barcodeNumber)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
            .setNewLinesAfter(1)
            .build())


    add(
        TextPrintable.Builder()
            .setText("ياندا ستور" )
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

    add(
        TextPrintable.Builder()
            .setText("القاهره")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

    add(
        TextPrintable.Builder()
            .setText("شبرا الخيمة")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(2)
            .build())




//    val qr: Bitmap = QRCode.from("RRN: : 234566dfgg4456\nAmount: NGN\$200,000\n")
//        .withSize(200, 200).bitmap()

//    add(
//        ImagePrintable.Builder(qr)
//            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//            .build())



    add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

}

/* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
        // There are no request codes
//            val intent = result.data
        printDetails()
    }
}



    private fun askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }



    }
    private var requestBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
            Log.d("permission", "granted")
            initView()
        }else{
            //deny
            Log.d("permission", "deny")
            toast("please accept the permission",context)
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }

            if (granted) {
                // your code if permission granted
                initView()
            } else {
                // your code if permission denied
                toast("please accept the permission",context)


            }


        }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

         val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )


        return bitmap
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess() {
            valueEd=binding.editTextBarcodeNumber.text.toString()
            displayBitmap(valueEd)

    }

    override fun onFailure(message: String) {
        toast(message, context)
    }
}


