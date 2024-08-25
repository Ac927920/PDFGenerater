package com.abhishek.generatepdfbylayout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.noButton)
        imageView = findViewById(R.id.imageView)

        button.setOnClickListener{
            val intent = Intent(this, Invoices::class.java)
            startActivity(intent)
        }
    }

//    private fun showInvoicesInDialog() {
//        val products = listOf(
//            Product(1, "Product A", 2, 50.0, 100.0),
//            Product(2, "Product B", 1, 150.0, 150.0),
//            Product(3, "Product C", 3, 30.0, 90.0)
//        )
//
//        val invoiceDetails = StringBuilder()
//        for (product in products) {
//            invoiceDetails.append("Serial Number: ${product.serialNumber}\n")
//            invoiceDetails.append("Description: ${product.description}\n")
//            invoiceDetails.append("Quantity: ${product.quantity}\n")
//            invoiceDetails.append("Rate: ${product.rate}\n")
//            invoiceDetails.append("Amount: ${product.amount}\n\n")
//        }
//
//        AlertDialog.Builder(this)
//            .setTitle("Invoice Details")
//            .setMessage(invoiceDetails.toString())
//            .setPositiveButton("OK") { _, _ ->
//                createAndDisplayPdf(products)
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }


    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs storage permissions to create and display PDFs.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                showInvoicesInDialog()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAndDisplayPdf(products: List<Product>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        headerOfPdf(page, products)
        pdfDocument.finishPage(page)

        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            pdfDocument.writeTo(byteArrayOutputStream)
            displayPdf(byteArrayOutputStream.toByteArray())
            Toast.makeText(this, "PDF created successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }

    private fun headerOfPdf(page: PdfDocument.Page, products: List<Product>) {
        val canvas = page.canvas
        val paint = Paint().apply {
            textSize = 16f
            color = Color.BLACK
        }

        val name = "Four Leaf Clover Agro Pvt. Ltd."
        val address = "Shop No. 2, Village Darau"
        val address1 = "Tehsil Kichha, U. S. Nagar"
        val gstData = "GSTIN/UIN: 05AACCF1463D1Z6"
        val stateName = "State Name: Uttarakhand, Code: 05"
        val cin = "CIN: U74140DL2013PTC249070"
        val email = "E-Mail: account@gram-unnati.com"

        // Draw the header text
        canvas.drawText(name, 30f, 30f, paint)
        canvas.drawText(address, 30f, 50f, paint)
        canvas.drawText(address1, 30f, 70f, paint)
        canvas.drawText(gstData, 30f, 90f, paint)
        canvas.drawText(stateName, 30f, 110f, paint)
        canvas.drawText(cin, 30f, 130f, paint)
        canvas.drawText(email, 30f, 150f, paint)

        // Draw product details
        var currentY = 180f
        for (product in products) {
            canvas.drawText("Serial Number: ${product.serialNumber}", 30f, currentY, paint)
            currentY += 20f
            canvas.drawText("Description: ${product.description}", 30f, currentY, paint)
            currentY += 20f
            canvas.drawText("Quantity: ${product.quantity}", 30f, currentY, paint)
            currentY += 20f
            canvas.drawText("Rate: ${product.rate}", 30f, currentY, paint)
            currentY += 20f
            canvas.drawText("Amount: ${product.amount}", 30f, currentY, paint)
            currentY += 30f // Add some space between products
        }

        // Draw a bitmap (image) with size 20x20 pixels
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 180, 130, true)
        canvas.drawBitmap(scaledBitmap, 0.02f * page.info.pageWidth.toFloat(), 0.05f * page.info.pageHeight.toFloat(), null)
    }

    private fun displayPdf(pdfContent: ByteArray) {
        try {
            val fileDescriptor = getParcelFileDescriptor(pdfContent)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val currentPage = pdfRenderer.openPage(0)

            val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            imageView.setImageBitmap(bitmap)

            currentPage.close()
            pdfRenderer.close()
            fileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getParcelFileDescriptor(pdfContent: ByteArray): ParcelFileDescriptor {
        val tempFile = File(cacheDir, "temp.pdf")
        val fos = FileOutputStream(tempFile)
        fos.write(pdfContent)
        fos.close()
        return ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 101
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

