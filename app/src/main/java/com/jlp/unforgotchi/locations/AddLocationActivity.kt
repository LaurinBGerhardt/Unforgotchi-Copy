package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.R
import java.io.FileDescriptor
import java.io.IOException


class AddLocationActivity : AppCompatActivity() {
    private lateinit var addLocNameView: TextInputEditText

    private val previewImage by lazy { findViewById<ImageButton>(R.id.selected_location_image_button) }
    private val selectImageFromGalleryResult  = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        //uri?.let { previewImage.setImageURI(uri) }
        uri?.let { previewImage.setImageBitmap(uriToBitmap(uri)) }
    }
    private fun selectImageFromGallery()  = selectImageFromGalleryResult.launch("image/*")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_location_layout)
        previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)

        addLocNameView = findViewById(R.id.add_name_of_location)
        previewImage.setOnClickListener {
            selectImageFromGallery()
        }

        findViewById<FloatingActionButton>(R.id.finish_adding_location).setOnClickListener {
            processInput()
        }
    }

    private fun processInput() {
        val intent = Intent()

        if (addLocNameView.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, intent)
        } else if (previewImage.drawable.equals(R.drawable.ic_baseline_image_search_24)
                    || previewImage.drawable == null) {
            previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)
            val name = addLocNameView.text.toString()
            intent.putExtra("name", name)
            setResult(Activity.RESULT_OK, intent)
        } else {
            val name = addLocNameView.text.toString()
            intent.putExtra("name", name)
            val img = previewImage.drawable.toBitmap()
            intent.putExtra("image",img)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFileDescriptor(fileDescriptor),
                100, 100,true
            )
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
