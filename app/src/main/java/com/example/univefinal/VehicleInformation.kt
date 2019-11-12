package com.example.univefinal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import java.net.URL

class VehicleInformation : AppCompatActivity() {
    private var parentView : String = ""

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
        parentView = intent.getStringExtra("parentView")
        loadVehicleData(strLicenseplate, infoTextView)
    }

    override fun onBackPressed() {
        super.onBackPressed()
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

    fun convertJSONtoLicenseplate(jsonArray: String) : MutableMap<String, String?>? {
        val map = mutableMapOf<String, String?>()

        //replace array chars
        val stripArray = jsonArray.replace("[", "").replace("]", "")
        if(stripArray != null)
        {
            //replace obj chars
            val stripObject = stripArray.replace("{", "").replace("}", "")
            //split on key-val
            val keyValPairs = stripObject.split(",")

            //loop through key value string array and get key and value
            for (row in keyValPairs)
            {
                val keyValPair = row.split(":")
                val keyString = keyValPair[0].replace("\"", "").replace("\"", "")
                val valueString = keyValPair[1].replace("\"", "").replace("\"", "")

                //add to map
                map.put(keyString, valueString)
            }
            return map
        }
        else {
            return null
        }
    }

    private fun returnToPreviousActivity() {
        val intent = Intent().apply {
            putExtra("errorToken", 1)
        }
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    private fun loadVehicleData(licenseplate : String, textView : TextView) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var loading = findViewById<RelativeLayout>(R.id.loader)
        loading.visibility = View.VISIBLE

        val licensePlateFormatted = licenseplate.replace("-", "")

        async{
            val apiURL = "https://opendata.rdw.nl/api/id/m9d7-ebf2.json?\$query=select%20%2A%20search%20%27$licensePlateFormatted%27%20limit%20100&\$\$query_timeout_seconds=3"
            val apiResult = getJsonFromURL(apiURL)

            uiThread {
                var car = convertJSONtoLicenseplate(apiResult)
                Log.d("all", car.toString())
                if(car != null) {
                    if(car["zuinigheidslabel"] == null)
                        car["zuinigheidslabel"] = "Onbekend"



                    var returnText =
                            car["merk"] +
                            "\n"+car["handelsbenaming"]+
                            "\n"+licenseplate +
                            "\n"+car["inrichting"]+
                            "\n"+car["uitvoering"]+
                            "\n"+car["zuinigheidslabel"]+
                            "\n"+car["voertuigsoort"]+
                            "\n"+car["aantal_deuren"]+
                            "\n"+car["eerste_kleur"]+
                            "\n"+formatAPKDate(car["vervaldatum_apk"])+
                            "\n"+formatAPKDate(car["datum_eerste_afgifte_nederland"])+
                            "\n €"+car["bruto_bpm"]

                    textView.text = returnText

                    //hide loader
                    loading.visibility = View.GONE

                    //labels
                    var labels = findViewById<TextView>(R.id.retrieved_info_labels)
                    labels.visibility = View.VISIBLE


                    //show premie button
//                    var premieBtn = findViewById<Button>(R.id.buttonCalcPremie)
//                    premieBtn.visibility = View.VISIBLE
                } else {
                    returnToPreviousActivity()
                }

            }
        }
    }

}
