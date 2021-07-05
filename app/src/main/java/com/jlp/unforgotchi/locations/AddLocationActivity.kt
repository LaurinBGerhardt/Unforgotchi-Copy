package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.R
import java.io.FileDescriptor
import java.io.IOException


class AddLocationActivity : AppCompatActivity() {
    private lateinit var addLocNameView: TextInputEditText

    private val previewImage by lazy { findViewById<ImageButton>(R.id.selected_location_image_button) }
    private var previewImageChanged : Boolean = false   //this is horrible coding dont copy this
    private var imageString : String? = null

    private val selectImageFromGalleryResult  = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            //previewImage.setImageBitmap(uriToBitmap(uri))
            previewImage.setImageURI(uri)
            imageString = uri.toString()
            previewImageChanged = true
        }
    }
    private fun selectImageFromGallery()  = selectImageFromGalleryResult.launch("image/*")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_location_layout)
        addLocNameView = findViewById(R.id.add_name_of_location)
        previewImage.setImageResource(R.drawable.ic_baseline_image_search_24)
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
        } else if ( !(previewImageChanged) || previewImage.drawable == null) {
            previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)
            val name = addLocNameView.text.toString()
            intent.putExtra("name", name)
            setResult(Activity.RESULT_OK, intent)
        } else {
            val name = addLocNameView.text.toString()
            intent.putExtra("name", name)
            //val img = previewImage.drawable.toBitmap()
            //intent.putExtra("image",img)
            intent.putExtra("image",imageString)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {

        val scaledscreenwidth :Double
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val outMetrics = resources.displayMetrics
            scaledscreenwidth = outMetrics.widthPixels / 2.0
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            scaledscreenwidth = displayMetrics.widthPixels / 2.0
        }

        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = Bitmap.createBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor))
            val imageheight = image.height.toFloat()
            val imagewidth = image.width.toFloat()
            val image2 = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFileDescriptor(fileDescriptor),
                scaledscreenwidth.toInt(),
                (scaledscreenwidth * (imageheight / imagewidth)).toInt(),
                true
            )
            parcelFileDescriptor.close()
            return image2
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
