package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jlp.unforgotchi.R

class AddLocationActivity : AppCompatActivity() {
    private lateinit var addLocNameView: TextInputEditText

    private val previewImage by lazy { findViewById<ImageView>(R.id.selected_location_image_button) }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { previewImage.setImageURI(uri) }
    }
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_location_layout)
        previewImage.setImageResource(R.drawable.ic_baseline_location_city_24)

        findViewById<FloatingActionButton>(R.id.finish_adding_location).setOnClickListener {
            processInput()
        }
        addLocNameView = findViewById(R.id.add_name_of_location)
        findViewById<ImageButton>(R.id.selected_location_image_button).setOnClickListener { selectImageFromGallery() }
    }

    private fun processInput() {
        val intent = Intent()

        if (addLocNameView.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, intent)
        } else {
            val name = addLocNameView.text.toString()
            intent.putExtra("name", name)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }
}
