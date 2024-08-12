package com.abhishek.generatepdfbylayout

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException


class Invoices : AppCompatActivity() {

    private lateinit var productContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoices)

        productContainer = findViewById(R.id.productContainer);

        showInvoicesInDialog()


    }

    private fun addProduct(productSr: String,productName: String,productQty:String,productRate:String,productAmount:String) {
        val inflater = LayoutInflater.from(this)
        val productView = inflater.inflate(R.layout.item_product, productContainer, false)
        val productSrTextView = productView.findViewById<TextView>(R.id.tvSerialNumber)
        val productNameTextView = productView.findViewById<TextView>(R.id.tvDescription)
        val productQtyTextView = productView.findViewById<TextView>(R.id.tvQuantity)
        val productRateTextView = productView.findViewById<TextView>(R.id.tvRate)
        val productAmountTextView = productView.findViewById<TextView>(R.id.tvAmount)
        productSrTextView.text = productSr
        productNameTextView.text = productName
        productQtyTextView.text = productQty
        productRateTextView.text = productRate
        productAmountTextView.text = productAmount

        productContainer.addView(productView)
    }
    private fun showInvoicesInDialog() {
        val products = listOf(
            Product(1, "Productbnmb,n.lmbjnklmlknbj Agksdv hiwehiowe bkjwbh bwebgo i", 2, 5000, 10000),
            Product(2, "Product B", 1, 15000, 15000),
            Product(3, "Product C", 3, 300, 900)
        )

        val invoiceDetails = StringBuilder()
        for (product in products) {
            invoiceDetails.append("Serial Number: ${product.serialNumber}\n")
            invoiceDetails.append("Description: ${product.description}\n")
            invoiceDetails.append("Quantity: ${product.quantity}\n")
            invoiceDetails.append("Rate: ${product.rate}\n")
            invoiceDetails.append("Amount: ${product.amount}\n\n")
            addProduct(product.serialNumber.toString(),product.description,product.quantity.toString(),product.rate.toString(),product.amount.toString())
        }

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
    }

    private fun createAndDisplayPdf(products: List<Product>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        pdfDocument.finishPage(page)

        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            pdfDocument.writeTo(byteArrayOutputStream)
            Toast.makeText(this, "PDF created successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }
}