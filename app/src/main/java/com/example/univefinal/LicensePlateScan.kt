package com.example.univefinal

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Visibility
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

import kotlinx.android.synthetic.main.activity_license_plate_scan.toolbar
import java.io.*

class LicensePlateScan : AppCompatActivity() {
    private val GALLERY = 1
    private val CAMERA = 2
    private lateinit var licensePlateText: EditText
    private lateinit var btnNextStep: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_plate_scan)
        setSupportActionBar(toolbar)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        /** Media Button **/
        val chooseMedia = findViewById<Button>(R.id.chooseSource)
        chooseMedia.setOnClickListener{
            showPictureDialog()
        }

        licensePlateText = findViewById<EditText>(R.id.licensePlateText)

        btnNextStep = findViewById<Button>(R.id.vehicle_information)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.to_main_menu) {
            Toast.makeText(this, "To Main menu", Toast.LENGTH_LONG).show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /** Image gallery **/
    fun showPictureDialog()
    {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Kies actie")
        val pictureDialogItems = arrayOf("Gallerij", "Camera")
        pictureDialog.setItems(pictureDialogItems,
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> choosePhotoFromGallary()
                    1 -> takePhotoFromCamera()
                }
            })
        pictureDialog.show()
    }
    fun choosePhotoFromGallary()
    {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    fun takePhotoFromCamera()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageview = findViewById<ImageView>(R.id.licensePicture)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageview.setImageBitmap(bitmap)

                    fetchLicensePlateFromBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Status:", "FAILED")
                }

            }

        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageview.setImageBitmap(thumbnail)

            fetchLicensePlateFromBitmap(thumbnail)
        }
    }

    /** License Plate **/
    fun fetchLicensePlateFromBitmap(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                processResultText(firebaseVisionText)
            }
            .addOnFailureListener {
                licensePlateText.setText("Failed")
            }
    }

    //TODO: stronger validation based on regexp
    private fun validLicensePlate(licensePlate : String) : Boolean {
        val ch = '-'
        var frequency = 0

        //check if char 3 = space (1,2 or 3 version of plate

        //find occurence of ch in licensePlate
        for (i in 0..licensePlate.length - 1) {
            if (ch == licensePlate[i]) {
                ++frequency
            }
        }

        if(licensePlate.length == 8) {
            //if slot 3 = empty or dash
            if(licensePlate.substring(2,3) == " ") {
                return true
            }

            if(frequency == 2) {
                return true
            }
        }

        return false
    }

    private fun processResultText(resultText: FirebaseVisionText) {
        //empty licenseplate text field
        licensePlateText.setText("")
        if (resultText.textBlocks.size == 0) {
            licensePlateText.setText("No Text Found")
            return
        }
        var blockText = ""
        for (block in resultText.textBlocks) {
            Log.d("RESPONSE LINE", block.text)
            if(validLicensePlate(block.text))
            {
                var value = block.text.replace(" ", "-")
                blockText = value            }
        }

        //none found
        if(blockText == "")
        {
            licensePlateText.visibility = View.INVISIBLE
            btnNextStep.visibility = View.INVISIBLE
        }
        else {
            //add license to textinput
            licensePlateText.setText(blockText)
            licensePlateText.visibility = View.VISIBLE

            btnNextStep.setOnClickListener{
                val intent = Intent(this, VehicleInformation::class.java)
                intent.putExtra("licenseplate", blockText)
                startActivity(intent)
            }

            btnNextStep.visibility = View.VISIBLE
        }
    }

}
