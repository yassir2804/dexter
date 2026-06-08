package com.dexter.app.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

object ImageUtils {

    private const val TAG = "ImageUtils"
    private const val MAX_SIZE = 256
    private const val QUALITY = 75

    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e(TAG, "openInputStream returned null for $uri")
                return null
            }
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (original == null) {
                Log.e(TAG, "decodeStream returned null")
                return null
            }
            Log.d(TAG, "Original image: ${original.width}x${original.height}")

            val scale = MAX_SIZE.toFloat() / maxOf(original.width, original.height)
            val w = (original.width * scale).toInt()
            val h = (original.height * scale).toInt()
            val resized = Bitmap.createScaledBitmap(original, w, h, true)
            if (resized !== original) original.recycle()

            val baos = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
            resized.recycle()

            val bytes = baos.toByteArray()
            val result = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
            Log.d(TAG, "Base64 encoded: ${result.length} chars (${bytes.size} bytes raw)")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to base64", e)
            null
        }
    }

    fun base64ToBitmap(dataUri: String): Bitmap? {
        return try {
            val base64 = dataUri.substringAfter("base64,")
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
