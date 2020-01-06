package com.example.univefinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.univefinal.AppMethods.Companion.getJsonFromURL
import com.example.univefinal.AppMethods.Companion.capitalizeWords
import com.example.univefinal.AppMethods.Companion.parseVehicleCosts
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import kotlinx.android.synthetic.main.content_vehicle_information.*
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.doAsync
import java.math.RoundingMode
import java.text.DecimalFormat

import kotlin.math.roundToInt


class VehicleInformation : AppCompatActivity() {

    private val valArray = arrayOf<String>("inrichting", "bouwjaar", "aantal_zitplaatsen", "aantal_deuren", "eerste_kleur", "massa_ledig_voertuig", "vervaldatum_apk")

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
        val infoTextView = findViewById<TextView>(R.id.general_info)
        loadVehicleData(strLicenseplate, infoTextView)

        //readmore button
        val info = findViewById<TextView>(R.id.general_info)
        val labels = findViewById<TextView>(R.id.general_info_labels)

        //collapse height
//        val oneLineHeight = (labels.getPaint().getFontMetrics().bottom - labels.getPaint().getFontMetrics().top).toInt()
//
//        //set init height
//        info.layoutParams.height = (oneLineHeight * 5)+5
//        labels.layoutParams.height = (oneLineHeight * 5)+5
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

    fun formatAPKDate(apkDate : String?) : String {
        val date = apkDate
        val formattedAPKYear = apkDate?.substring(0, 4)

        val formattedAPKMonth = date?.substring(4, 6)
        val formattedAPKDay = date?.substring(6, 8)

        return formattedAPKDay+"-"+formattedAPKMonth+"-"+formattedAPKYear
    }

    fun convertJSONtoLicenseplate(jsonArray: String) : MutableMap<String, String?>? {
        val map = mutableMapOf<String, String?>()

        Log.d("STRING", jsonArray)

        //replace array chars
        val stripArray = jsonArray.replace("[", "").replace("]", "")
        if(stripArray != null)
        {
            //replace obj chars
            val stripObject = stripArray.replace("{", "").replace("}", "")
            //split on key-val
            val keyValPairs = stripObject.split("\",")
            if(keyValPairs.size > 1) {
                //loop through key value string array and get key and value
                for (row in keyValPairs) {
                    val keyValPair = row.split(":")
                    Log.d("acd", keyValPair.toString())
                    val keyString = keyValPair[0].replace("\"", "").replace("\"", "")
                    var valueString = keyValPair[1].replace("\"", "").replace("\"", "")
                    //add to map
                    map.put(keyString, valueString)

                    valArray.forEach{
                        if(map[it] == null  || map[it] == "niet geregistreerd") {
                            map[it] = "Onbekend"
                        }else if(map[it] is String){
                            map[it] = map[it]?.toLowerCase()?.capitalize()
                        }
                    }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }



    private fun loadVehicleCosts(licenseplate: String, loading : RelativeLayout) {

        doAsync {
            val apiURL ="https://scr.autodisk.nl/wsTCO_Client/wsTCO.asmx/wsCalculeerTCO?nDebiteurNummer=4533429&strGebruikersnaam=Unive.NL&strWachtwoord=yhnK@uQ=53XWG23V%2597rbkg&strKenteken=" + licenseplate + "&nKilometerStand=25000&nInzetLooptijd=24&nInzetKmPerJaar=10000&sngRentePercentage=3&nAantalMaandenInBezit=11"
            val apiResult = getJsonFromURL(apiURL)

            uiThread {
                var vehicleCosts = parseVehicleCosts(apiResult)
                if (vehicleCosts.size != 0) {
                    var costsMap = mutableMapOf(
                        "Totaal" to vehicleCosts[0].getValue("TCO_Totaal"),
                        "AfschrijvingEnRente" to vehicleCosts[0].getValue("AfschrijvingEnRente"),
                        "ReparatieEnOnderhoud" to vehicleCosts[0].getValue("ReparatieEnOnderhoud"),
                        "Banden" to vehicleCosts[0].getValue("Banden"),
                        "Belasting" to vehicleCosts[0].getValue("MRB"),
                        "Verzekering" to vehicleCosts[0].getValue("Verzekering")
                    )

                    var formattedCostsMap = mutableMapOf<String, String>()
                    val df = DecimalFormat("###.00")
                    df.roundingMode = RoundingMode.CEILING
                    costsMap.forEach { (key, value) ->
                        var x = value.replace(",", ".")
                        x = df.format(x.toDouble())
                        x = x.replace(".", ",")
                        if(x == ",00"){
                            x = "0,00"
                        }
                        formattedCostsMap[key] = x
                    }

                    var costsTextView = findViewById<TextView>(R.id.vehicle_costs)
                    costsTextView.text = "€ " + formattedCostsMap["AfschrijvingEnRente"] +
                            "\n€ " + formattedCostsMap["Belasting"] +
                            "\n€ " + formattedCostsMap["Verzekering"] +
                            "\n€ " + formattedCostsMap["ReparatieEnOnderhoud"] +
                            "\n€ " + formattedCostsMap["Banden"]

                    var totalCostsTextView = findViewById<TextView>(R.id.total_costs_label)
                    totalCostsTextView.text = "€ " + costsMap["Totaal"] + " p.m."

                } else {
                    var costsTitle = findViewById<TextView>(R.id.vehicleCostsTitle)
                    costsTitle.visibility = View.GONE

                    var costsCardView = findViewById<CardView>(R.id.cardViewCosts)
                    costsCardView.visibility = View.GONE
                }
                //hide loader
                loading.visibility = View.GONE

                //show premie button
                var premieBtn = findViewById<Button>(R.id.buttonCalcPremie)
                premieBtn.visibility = View.VISIBLE
            }
        }
    }


    private fun loadVehicleData(licenseplate : String, textView : TextView) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var loading = findViewById<RelativeLayout>(R.id.loaderOverlay)
        loading.visibility = View.VISIBLE
        val licensePlateFormatted = licenseplate.replace("-", "")

        doAsync {
            val apiURL = "https://opendata.rdw.nl/api/id/m9d7-ebf2.json?\$query=select%20%2A%20search%20%27$licensePlateFormatted%27%20limit%20100&\$\$query_timeout_seconds=3"
            val fuelApiURL = "https://opendata.rdw.nl/api/id/8ys7-d773.json?\$query=select%20*%20search%20%27$licensePlateFormatted%27%20limit%20100&\$\$query_timeout_seconds=30"

            val apiResult = getJsonFromURL(apiURL)
            val fuelApiResult = getJsonFromURL(fuelApiURL)
            //load vehicle costs from API
            loadVehicleCosts(licensePlateFormatted, loading)

            uiThread {
                var car = convertJSONtoLicenseplate(apiResult)
                var fueldata = convertJSONtoLicenseplate(fuelApiResult)
                Log.d("all ", car.toString())
                Log.d("fueldata ", fueldata.toString())
                if(car != null && fueldata != null) {

                    var catalogusprijs = "Onbekend"
                    if(car["catalogusprijs"] != null){
                        catalogusprijs = "€" + car["catalogusprijs"]
                    }
                    var brandstofType = "Onbekend"
                    if(fueldata["brandstof_omschrijving"] != null){
                        brandstofType = fueldata["brandstof_omschrijving"]?.toLowerCase()!!.capitalize()
                    }
                    var vermogen = "Onbekend"
                    if(fueldata["nettomaximumvermogen"] != null){
                        vermogen = fueldata["nettomaximumvermogen"]?.toFloat()?.roundToInt().toString() + " kW / " + (fueldata["nettomaximumvermogen"]!!.toFloat() * 1.362).roundToInt().toString() + " pk"
                    }
                    var verbruik = "Onbekend"
                    if(fueldata["brandstofverbruik_gecombineerd"] != null) {
                        verbruik = (fueldata["brandstofverbruik_gecombineerd"]?.toFloat()).toString() + "l / 100km"
                    }

                    //Build data string for general card
                    var generalText = car["inrichting"] +
                            "\n" + formatAPKDate(car["datum_eerste_afgifte_nederland"]).split("-")[2] +
                            "\n" + car["aantal_zitplaatsen"] +
                            "\n" + car["aantal_deuren"] +
                            "\n" + car["eerste_kleur"]

                    //Build data string for vehicle card
                    var vehicleText = formatAPKDate(car["vervaldatum_apk"]) +
                            "\n" + brandstofType +
                            "\n" + car["massa_ledig_voertuig"] + " kg" +
                            "\n" + vermogen +
                            "\n" + verbruik

                    var merk = car["merk"]?.toLowerCase()?.capitalize()
                    var handelsBenaming = car["handelsbenaming"]?.toLowerCase()?.capitalizeWords()
                    var title = findViewById<TextView>(R.id.textView)

                    //Set car title text, check if manufacturer name is not in car trade name
                    handelsBenaming = handelsBenaming?.replace(merk.toString() + " ", "")
                    title.text = merk + " " + handelsBenaming

                    textView.text = generalText
                    var vehicleTextView = findViewById<TextView>(R.id.vehicle_info)
                    vehicleTextView.text = vehicleText

                    //labels
                    var labels = findViewById<TextView>(R.id.general_info_labels)
                    labels.visibility = View.VISIBLE

                    var licensePlateInput = findViewById<TextView>(R.id.license_plate_input2)
                    licensePlateInput.text = licenseplate
                } else {
                    licensePlateError()
                }

            }
        }
    }
}
