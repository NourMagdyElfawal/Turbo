package com.example.turbo.generatebarcode.components.presentation

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.turbo.R
import com.example.turbo.databinding.FragmentGenerateBarcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer

class GenerateBarcodeFragment : Fragment() {

    companion object {
        fun newInstance() = GenerateBarcodeFragment()
    }

    private var barcodeNumber: String=""
    private lateinit var viewModel: GenerateBarcodeViewModel
    private lateinit var binding: FragmentGenerateBarcodeBinding
    private lateinit var valueEd: String


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
    private fun displayBitmap(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)

        binding.imageBarcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = resources.getColor(android.R.color.holo_blue_bright),
                backgroundColor = resources.getColor(android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
        barcodeNumber=binding.editTextBarcodeNumber.getText().toString()
        if (barcodeNumber.isNotEmpty()) {
            binding.textViewBarcodeNumber.setText(barcodeNumber)
            binding.editTextBarcodeNumber.visibility = View.GONE
            binding.buttonGenerateBarcode.visibility = View.GONE
            binding.buttonPrint.visibility = View.VISIBLE
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


