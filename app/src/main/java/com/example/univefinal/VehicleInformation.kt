package com.example.univefinal

import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import java.net.URL

class VehicleInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_information)
        setSupportActionBar(toolbar)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        val strLicenseplate = intent.getStringExtra("licenseplate")
        val infoTextView = findViewById<TextView>(R.id.retrieved_info)
        loadVehicleData(strLicenseplate, infoTextView)
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

    fun getJsonFromURL(wantedURL: String) : String {
        val text = URL(wantedURL).readText()
        return text
    }

    fun formatAPKDate(apkDate : String?) : String {
        val date = apkDate
        val formattedAPKYear = apkDate?.substring(0, 4)

        val formattedAPKMonth = date?.substring(4, 6)
        val formattedAPKDay = date?.substring(6, 8)

        return formattedAPKDay+"-"+formattedAPKMonth+"-"+formattedAPKYear
    }

    fun convertJSONtoLicenseplate(jsonArray: String) : LicensePlate? {
        val resultArray = Klaxon().parseArray<LicensePlate>(jsonArray)
        if(resultArray != null && resultArray.isNotEmpty()) {
            val car = resultArray?.get(0)
            return car
        }
        else {
            return null
        }
    }

    private fun loadVehicleData(licenseplate : String, textView : TextView) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

//        var loading = findViewById<RelativeLayout>(R.id.loadingPanel)
//        loading.visibility = View.VISIBLE

        val licensePlateFormatted = licenseplate.replace("-", "")
        Log.d("Plate:", licensePlateFormatted)

        async{
            val apiURL = "https://opendata.rdw.nl/api/id/m9d7-ebf2.json?\$query=select%20%2A%20search%20%27$licensePlateFormatted%27%20limit%20100&\$\$query_timeout_seconds=3"
            val apiResult = getJsonFromURL(apiURL)
            var car = convertJSONtoLicenseplate(apiResult)
            uiThread {
                if(car != null) {
                    Log.d("Car", car.toString())
                    var returnText = licenseplate + "\n" +car.merk + "\n" +car.handelsbenaming+"\n"+car.brandstof+"\n"+car.inrichting+"\n"+formatAPKDate(car.vervaldatum_apk)
                    textView.text = returnText

//                    //hide loader
//                    loading.visibility = View.GONE

                    //labels
                    var labels = findViewById<TextView>(R.id.retrieved_info_labels)
                    labels.visibility = View.VISIBLE


                    //show premie button
//                    var premieBtn = findViewById<Button>(R.id.buttonCalcPremie)
//                    premieBtn.visibility = View.VISIBLE
                }

            }
        }
    }

}
