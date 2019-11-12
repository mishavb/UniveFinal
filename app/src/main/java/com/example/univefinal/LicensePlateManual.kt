package com.example.univefinal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_license_plate_manual.*
import kotlinx.android.synthetic.main.content_license_plate_manual.*

class LicensePlateManual : AppCompatActivity() {
    companion object {
        const val START_VEHICLE_INFO_REQUEST_CODE = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_plate_manual)
        setSupportActionBar(toolbar)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        //button actions
        val retrieveVehicleInfo = findViewById<Button>(R.id.vehicle_information)
        retrieveVehicleInfo.setOnClickListener{
            val licenseplateinput = findViewById<EditText>(R.id.license_plate_input)
            val licenseplatetext = licenseplateinput.text.toString()
            val errorlabel = findViewById<TextView>(R.id.errorlabel)
            errorlabel.text = ""

            if(validateLicensePlate(licenseplatetext)) {
                val intent = Intent(this, VehicleInformation::class.java)
                intent.putExtra("licenseplate", licenseplatetext)
                intent.putExtra("parentView", "manual")
                startActivityForResult(intent, START_VEHICLE_INFO_REQUEST_CODE)
            } else {
                errorlabel.text = "Vul een geldig kenteken in"
            }
        }

        //scan licenseplate button
        var retrieve_vehicle_info3 = findViewById<Button>(R.id.retrieve_vehicle_info3)
        retrieve_vehicle_info3.setOnClickListener{
            val intent = Intent(this, LicensePlateScan::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_VEHICLE_INFO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val errorToken = data!!.getIntExtra("errorToken", 0)
                if(errorToken == 1){
                    errorlabel.text = "Voor dit kenteken zijn geen gegevens beschikbaar"
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId
        AppMethods.returnToMainMenu(id, this)

        return super.onOptionsItemSelected(item)
    }

    private fun validateLicensePlate(licenseplate : String) : Boolean {
        val check = "-"
        if(licenseplate.length != 8) {
            return false
        } else if (licenseplate.count{ check.contains(it) } != 2){
            return false
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}