package com.example.flashmind.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun uploadPdfBytesToServer(pdfBytes: ByteArray, deckName: String, userId: Int) {
    withContext(Dispatchers.IO) {
        val client = HttpClient(Android)

        try {
            Log.d("PdfUpload", "Trimit PDF-ul către server ($deckName)...")

            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "http://192.168.1.130:8080/upload-pdf",
                //10.0.2.2
                //192.168.1.130
                formData = formData {
                    append("deckName", deckName)
                    append("userId", userId.toString())
                    append("pdf", pdfBytes, Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, "filename=\"document.pdf\"")
                    })
                }
            )
            Log.d("PdfUpload", "SUCCES: ${response.bodyAsText()}")

        } catch (e: Exception) {
            Log.e("PdfUpload", "EROARE: ${e.message}")
        } finally {
            client.close()
        }
    }
}