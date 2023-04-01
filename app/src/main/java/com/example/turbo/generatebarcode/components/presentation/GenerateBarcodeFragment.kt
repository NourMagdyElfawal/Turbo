package com.example.turbo.generatebarcode.components.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.Settings
import android.text.Layout.Alignment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.example.turbo.R
import com.example.turbo.databinding.FragmentGenerateBarcodeBinding
import com.example.turbo.generatebarcode.components.presentation.model.ItemModel
import com.example.turbo.printPdfFeature.PDFUtils
import com.example.turbo.printPdfFeature.PdfDocumentAdapter
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*


class GenerateBarcodeFragment : Fragment() {

    companion object {
        fun newInstance() = GenerateBarcodeFragment()

        private val FILE_PRINT: String="PDFprint.pdf"
        fun getBitmapImage(model: ItemModel,document: Document)
        :Observable<ItemModel>{
            return Observable.fromCallable {
                val image=Image.getInstance(bitmapToBytArray(model.itemImageBarcodeBitmap))
                image.scaleAbsolute(400f,100f)
                image.alignment=Image.ALIGN_CENTER
                document.add(image)
                model
            }
        }

        private fun bitmapToBytArray(bitmap: Bitmap?): ByteArray {
            val stream=ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG,100,stream)
            return stream.toByteArray()
        }
    }
    val model=ItemModel()
    private var barcodeNumber: String=""
    private lateinit var viewModel: GenerateBarcodeViewModel
    private lateinit var binding: FragmentGenerateBarcodeBinding
    private lateinit var valueEd: String
    var itemModelList=ArrayList<ItemModel>()
    private val appPath:String
        private get(){
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            val dir = File(extStorageDirectory)
            if (!dir.exists())
                dir.mkdirs()
            val file: File
            file = File(extStorageDirectory, "/TestFolder")

            if (file.exists()) {
                file.delete()
                file.createNewFile()
                Log.e("TAG","exists")
            } else {
                file.createNewFile()
                Log.e("TAG","NotExist")

            }
            return dir.path+File.separator
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_generate_barcode, container, false
            )


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GenerateBarcodeViewModel::class.java)

        Log.e("TAG","GenerateBarcodeFragment")
        // TODO: Use the ViewModel
        // set on-click listener
        binding.buttonGenerateBarcode.setOnClickListener {
            // your code to perform when the user clicks on the button
            valueEd=binding.editTextBarcodeNumber.text.toString()

            displayBitmap(valueEd)

        }


    }

    private fun createPDFFile(path: String) {
        if(File(path).exists()){
            File(path).delete()
        }
        try {

            val document=Document()
            //save
          val writer =PdfWriter.getInstance(document, FileOutputStream(path))

            //open
            document.open()

            //setting
//            document.pageSize=PageSize.A4
//            document.addCreationDate()
            document.addAuthor("Nour")
//            document.addCreator("kk")

            //font setting
            val colorAccent=BaseColor(0,153,204,255)
            val fontSize=20.0f

            //custom font
            val font=BaseFont.createFont("assets/fonts/brandon_medium.otf","UTF-8",BaseFont.EMBEDDED)
            val fontArabic=BaseFont.createFont("assets/fonts/NotoNaskhArabic-Regular.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED)

//            create title of document
            val titleFont=Font(font,36.0f,Font.NORMAL,BaseColor.BLACK)
            val titleFontArabic=Font(fontArabic,36.0f,Font.NORMAL,BaseColor.BLACK)

            //use RXjava to fetch image and add to PDF
            Observable.fromIterable(itemModelList)
                .flatMap {model:ItemModel->getBitmapImage(model,document)}
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { model:ItemModel->
                    //onNext
                        Log.e("TAG","onNext")
                        PDFUtils.addLinesSeparator(document)
                        PDFUtils.addNewItem(document,model.itemScannedBarcode!!,Element.ALIGN_CENTER,titleFont)
                        PDFUtils.addLinesSeparator(document)
                        PDFUtils.addNewItemArabic(document,model.itemStore!!,titleFontArabic)
                        PDFUtils.addLinesSeparator(document)
                        PDFUtils.addNewItemArabic(document,model.itemCity!!,titleFontArabic)
                        PDFUtils.addLinesSeparator(document)
                        PDFUtils.addNewItemArabic(document,model.itemUOM!!,titleFontArabic)
                        PDFUtils.addLinesSeparator(document)
                        PDFUtils.addNewItemArabic(document,model.itemScannedQR_Code!!,titleFontArabic)
                        PDFUtils.addLinesSeparator(document)

                    },
                    {
                    t:Throwable? ->
                //on Error
                        Log.e("TAG","Error")

                        Toast.makeText(context,t!!.message,Toast.LENGTH_SHORT).show()

                    },{
                //on Complet
                PDFUtils.addLineSpace(document)
                //close
                document.close()
                Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show()
                printPDF()
            })
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }catch (e:DocumentException){
            e.printStackTrace()
        }finally {

        }
    }

    private fun printPDF() {
        val printManager= requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printDocumentAdapter=PdfDocumentAdapter(java.lang.StringBuilder(appPath).append(
                FILE_PRINT).toString(), FILE_PRINT)
            printManager.print("Document",printDocumentAdapter,
            PrintAttributes.Builder().build())
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun initModel(bitmap: Bitmap, barcodeNumber: String) {
        Log.e("TAG",bitmap.toString())
        Log.e("TAG",barcodeNumber)

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

//                Dexter.withActivity(activity)
//                    .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .withListener(object:PermissionListener{
//                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            binding.buttonPrint.setOnClickListener {
                                askForPermissions()
                            }
//                        }
//
//                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
//                            Toast.makeText(context,"please enable this permission",
//                                Toast.LENGTH_SHORT).show()
//                        }
//
//                        override fun onPermissionRationaleShouldBeShown(
//                            permission: PermissionRequest?,
//                            token: PermissionToken?
//                        ) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
//                    .check()


        }
        initModel(bitmap,barcodeNumber)
    }

    private fun askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
            createPDFFile(StringBuilder(appPath).append(FILE_PRINT).toString())
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


}


