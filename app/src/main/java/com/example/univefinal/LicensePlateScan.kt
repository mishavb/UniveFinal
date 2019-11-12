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

    companion object {
        const val START_VEHICLE_INFO_REQUEST_CODE = 0
    }

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
        if (requestCode == START_VEHICLE_INFO_REQUEST_CODE){
            Toast.makeText(this, "Geen gegevens over dit kenteken gevonden", Toast.LENGTH_SHORT).show()
        } else if (requestCode == GALLERY) {
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

    fun validLicensePlate(licensePlate : String) : Boolean
    {
        val LP = licensePlate.replace("-", "").replace("-", "")
        val regexArray = arrayOf(
            Regex("/^([A-Z]{2})(\\d{2})(\\d{2})$/"),  // 1     XX-99-99    (since 1951)
            Regex("/^(\\d{2})(\\d{2})([A-Z]{2})$"), // 2     99-99-XX    (since 1965)
            Regex("^(\\d{2})([A-Z]{2})(\\d{2})$"),  // 3     99-XX-99    (since 1973)
            Regex("^([A-Z]{2})(\\d{2})([A-Z]{2})$"), // 4     XX-99-XX    (since 1978)
            Regex("^([A-Z]{2})([A-Z]{2})(\\d{2})$"),  // 5     XX-XX-99    (since 1991)
            Regex("^(\\d{2})([A-Z]{2})([A-Z]{2})$"),  // 6     99-XX-XX    (since 1999)
            Regex("^(\\d{2})([A-Z]{3})(\\d{1})$"), // 7     99-XXX-9    (since 2005)
            Regex("^(\\d{1})([A-Z]{3})(\\d{2})$"), // 8     9-XXX-99    (since 2009)
            Regex("^([A-Z]{2})(\\d{3})([A-Z]{1})$"),  // 9     XX-999-X    (since 2006)
            Regex("^([A-Z]{1})(\\d{3})([A-Z]{2})$"),  // 10    X-999-XX    (since 2008)
            Regex("^([A-Z]{3})(\\d{2})([A-Z]{1})$"), // 11    XXX-99-X    (since 2015)
            Regex("^([A-Z]{1})(\\d{2})([A-Z]{3})$"),  // 12    X-99-XXX
            Regex("^(\\d{1})([A-Z]{2})(\\d{3})$"),  // 13    9-XX-999
            Regex("^(\\d{3})([A-Z]{2})(\\d{1})$"))  // 14    999-XX-9

        var found = false
        for(regexp in regexArray) {
            if(LP.matches(regexp))
            {
                found = true
            }
        }

        return found
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
            if(validLicensePlate(block.text) && block.text != "No Text")
            {
                var value = block.text.replace(" ", "-")
                blockText = value
            }
        }
        val licenseView = findViewById<ImageView>(R.id.licenseView)

        //none found
        if(blockText == "")
        {
            licensePlateText.visibility = View.INVISIBLE
            btnNextStep.visibility = View.INVISIBLE
            licenseView.visibility = View.INVISIBLE
        }
        else {
            //show licenceplate graphic
            licenseView.visibility = View.VISIBLE

            //add license to textinput
            licensePlateText.setText(blockText)
            licensePlateText.visibility = View.VISIBLE

            btnNextStep.setOnClickListener{
                val intent = Intent(this, VehicleInformation::class.java)
                intent.putExtra("licenseplate", licensePlateText.text.toString())
                intent.putExtra("parentView", "scan")
                startActivityForResult(intent, START_VEHICLE_INFO_REQUEST_CODE)
            }

            btnNextStep.visibility = View.VISIBLE
        }
    }

}
