package com.example.turbo.printPdfFeature

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.*

class PdfDocumentAdapter(var path:String,var fileName:String):PrintDocumentAdapter(){
    override fun onLayout(
        printAttributes: PrintAttributes?,
        printAttributes1: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        layoutResultCallback: LayoutResultCallback?,
        p4: Bundle?
    ) {

        if(cancellationSignal!!.isCanceled) layoutResultCallback!!
            .onLayoutCancelled()
        else{
            val builder=PrintDocumentInfo.Builder(fileName)
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            layoutResultCallback!!.onLayoutFinished(builder.build(),
            printAttributes1 !=printAttributes)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onWrite(
        pageRange: Array<out PageRange>?,
        parcelFileDescriptor: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        writeResultCallback: WriteResultCallback?
    ) {
        var input: InputStream? =null
        var output: OutputStream? =null
        try {
            val file= File(path)
            input=FileInputStream(file)
            output=FileOutputStream(parcelFileDescriptor!!.fileDescriptor)
            val buff=ByteArray(16384)
            var size:Int
            while (input.read(buff).also { size=it }>=0
                && !cancellationSignal!!.isCanceled)output.write(buff,0,size)
                if (cancellationSignal!!.isCanceled)writeResultCallback!!.onWriteCancelled()
                else{
                   writeResultCallback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                }
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }finally {
            try {
                input!!.close()
                output!!.close()
            }catch (e:IOException) {
                e.printStackTrace()
            }
            }

    }
}