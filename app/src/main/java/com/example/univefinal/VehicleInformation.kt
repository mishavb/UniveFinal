package com.example.univefinal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils.split
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import java.net.URL
import com.example.univefinal.AppMethods
import org.jetbrains.anko.custom.asyncResult


class VehicleInformation : AppCompatActivity() {
    private var parentView : String = ""


    override fun onStart() {
        super.onStart()

        if(!AppMethods.isOnline(this)){
            val intent = Intent(this, NetworkError::class.java)
            startActivity(intent)
        }
    }

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

        //readmore button
        val info = findViewById<TextView>(R.id.retrieved_info)
        val labels = findViewById<TextView>(R.id.retrieved_info_labels)

        //collapse height
        val oneLineHeight = (labels.getPaint().getFontMetrics().bottom - labels.getPaint().getFontMetrics().top).toInt()

        //set init height
        info.layoutParams.height = (oneLineHeight * 5)+5
        labels.layoutParams.height = (oneLineHeight * 5)+5
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
        AppMethods.returnToMainMenu(id, this)

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
            if(keyValPairs.size > 1) {
                //loop through key value string array and get key and value
                for (row in keyValPairs) {
                    val keyValPair = row.split(":")
                    val keyString = keyValPair[0].replace("\"", "").replace("\"", "")
                    val valueString = keyValPair[1].replace("\"", "").replace("\"", "")

                    //add to map
                    map.put(keyString, valueString)
                }
                return map
            } else {
                return null
            }
        }
        else {
            return null
        }
    }

    private fun licensePlateError() {
        val intent = Intent(this, LicenseError::class.java)
        intent.putExtra("parentView", parentView)

        startActivity(intent)
    }

    private fun getRoadTax(weight : Int, fuel : String) : String{
        var fuelInt = 0
        when(fuel){
            "benzine" -> fuelInt = 1
            "diesel" -> fuelInt = 2
            "CNG" -> fuelInt = 3
            "LPG" ->fuelInt = 4
        }
        val apiURL = "http://bmdigital.nl/unive/?kg=$weight&prov=UT&fuel=$fuelInt"
        return apiURL
    }

    private fun getFuel(licenseplate: String){
        val apiURL = "https://opendata.rdw.nl/api/id/8ys7-d773.json?\$query=select%20*%20search%20%27$licenseplate%27%20limit%20100&\$query_timeout_seconds=30"
        async {
            val apiResult = getJsonFromURL(apiURL)

           asyncResult {
           }
        }
    }

    private fun loadVehicleData(licenseplate : String, textView : TextView) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var loading = findViewById<RelativeLayout>(R.id.loader)
        loading.visibility = View.VISIBLE

        val licensePlateFormatted = licenseplate.replace("-", "")
        async { }
        async{
            val apiURL = "https://opendata.rdw.nl/api/id/m9d7-ebf2.json?\$query=select%20%2A%20search%20%27$licensePlateFormatted%27%20limit%20100&\$query_timeout_seconds=3"
            val apiResult = getJsonFromURL(apiURL)

            uiThread {
                var car = convertJSONtoLicenseplate(apiResult)
                Log.d("all", car.toString())
                if(car != null) {
                    if(car["zuinigheidslabel"] == null)
                        car["zuinigheidslabel"] = "Onbekend"

                    if(car["uitvoering"] == null)
                        car["uitvoering"] = "Onbekend"

                    var bpm = ""
                    if(car["bruto_bpm"] == null)
                        bpm = "Niet bekend"
                    else {
                        bpm = "€ "+car["bruto_bpm"]
                    }

                    var returnText =
                            car["uitvoering"] +
                            "\n"+car["eerste_kleur"]

                    var merk = car["merk"]?.toLowerCase()?.capitalize()
                    var handelsBenaming = car["handelsbenaming"]?.toLowerCase()?.capitalizeWords()
                    var title = findViewById<TextView>(R.id.textView)

                    //Set car title text, check if manufacturer name is not in car trade name
                    if(" " in handelsBenaming!!){
                        if(handelsBenaming.split(" ")[0] == merk) {
                            title.text = handelsBenaming
                        } else {
                            title.text = merk + " " + handelsBenaming
                        }
                    } else {
                        title.text = merk + " " + handelsBenaming
                    }
                    textView.text = returnText

                    //set APK date text
                    var textViewAPK = findViewById<TextView>(R.id.textViewAPK)
                    textViewAPK.text = formatAPKDate(car["vervaldatum_apk"])

                    //Set energylabel text
                    if(car["zuinigheidslabel"] != "Onbekend") {
                        var textViewEnergy = findViewById<TextView>(R.id.textViewEnergy)
                        textViewEnergy.text = "Energie " + car["zuinigheidslabel"]
                    }

                    //Set seats text
                    var textViewSeats = findViewById<TextView>(R.id.textViewSeats)
                    textViewSeats.text = car["aantal_zitplaatsen"]

                    //Set body text
                    var textViewBody = findViewById<TextView>(R.id.textViewBody)
                    textViewBody.text = car["inrichting"]?.toLowerCase()?.capitalize()

                    //hide loader
                    loading.visibility = View.GONE

                    //labels
                    var labels = findViewById<TextView>(R.id.retrieved_info_labels)
                    labels.visibility = View.VISIBLE

                    var licensePlateInput = findViewById<TextView>(R.id.license_plate_input2)
                    licensePlateInput.text = licenseplate

                    //show premie button
                    var premieBtn = findViewById<Button>(R.id.buttonCalcPremie)
                    premieBtn.visibility = View.VISIBLE
                } else {
                    licensePlateError()
                }

            }
        }
    }
    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
}
