package com.example.turbo.printPdfFeature

import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark

object PDFUtils {
    @Throws(DocumentException::class)
    fun addNewItem(document:Document,text: String,align:Int,font: Font){
        val chunk=Chunk(text!!,font!!)
        val p=Paragraph(chunk)
        p.alignment=align
        document.add(p)
    }

    @Throws(DocumentException::class)
    fun addLinesSeparator(document: Document){
        val lineSeparator=LineSeparator()
        lineSeparator.lineColor= BaseColor(0,0,0,68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)

    }

    @Throws(DocumentException::class)
     fun addLineSpace(document: Document) {
        document.add(Paragraph(""))

    }

    @Throws(DocumentException::class)
    fun addNewItemWithLeftAndRight(document:Document,leftText: String,
                                   rightText: String,leftFont: Font,rightFont: Font){
        val chunkTextLeft=Chunk(leftText,leftFont)
        val chunkTextRight=Chunk(rightText,rightFont)
        val p=Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }
    @Throws(DocumentException::class)
    fun addNewItemArabic(document:Document,arabicText: String,titleFontArabic: Font){
        val table = PdfPTable(1)
        table.runDirection = PdfWriter.RUN_DIRECTION_RTL
        val cell = PdfPCell(Paragraph(arabicText, titleFontArabic))
        cell.setBorder(Rectangle.NO_BORDER)
        table.addCell(cell)
        document.add(table)
    }

}