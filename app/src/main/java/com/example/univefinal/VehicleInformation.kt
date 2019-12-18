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
import kotlinx.android.synthetic.main.activity_vehicle_information.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import java.net.URL
import org.jetbrains.anko.doAsync
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.math.roundToInt


class VehicleInformation : AppCompatActivity() {


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
            val apiURL = "https://scr.autodisk.nl/wsTCO_Client/wsTCO.asmx/wsCalculeerTCO?nDebiteurNummer=4533429&strGebruikersnaam=Unive.NL&strWachtwoord=yhnK@uQ=53XWG23V%2597rbkg&strKenteken="+licenseplate+"&nKilometerStand=25000&nInzetLooptijd=24&nInzetKmPerJaar=10000&sngRentePercentage=3&nAantalMaandenInBezit=11"
            val apiResult = getJsonFromURL(apiURL)
            uiThread {
                var vehicleCosts = parseVehicleCosts(apiResult)
                if(vehicleCosts.size != 0) {
                    val TCO_Totaal = vehicleCosts[0].getValue("TCO_Totaal")
                    val AfschrijvingEnRente = vehicleCosts[0].getValue("AfschrijvingEnRente")
                    val ReparatieEnOnderhoud = vehicleCosts[0].getValue("ReparatieEnOnderhoud")
                    val Banden = vehicleCosts[0].getValue("Banden")
                    val Belasting = vehicleCosts[0].getValue("MRB")
                    val Verzekering = vehicleCosts[0].getValue("Verzekering")
                    var costsTextView = findViewById<TextView>(R.id.vehicle_costs)
                    costsTextView.text = "€ " + AfschrijvingEnRente +
                            "\n€ " + Belasting +
                            "\n€ " + Verzekering +
                            "\n€ " + ReparatieEnOnderhoud +
                            "\n€ " + Banden

                    var totalCostsTextView = findViewById<TextView>(R.id.total_costs_label)
                    totalCostsTextView.text =  "€ " + TCO_Totaal + " p.m."
                    Log.d("Totale kosten", TCO_Totaal)
                    Log.d("AfschrijvingEnRente", AfschrijvingEnRente)
                    Log.d("ReparatieEnOnderhoud", ReparatieEnOnderhoud)
                    Log.d("Banden", Banden)
                    Log.d("Belasting", Belasting)
                    Log.d("Verzekering", Verzekering)


                    //hide loader
                    loading.visibility = View.GONE

                    //show premie button
                    var premieBtn = findViewById<Button>(R.id.buttonCalcPremie)
                    premieBtn.visibility = View.VISIBLE

                }
            }
        }
    }

    private fun parseVehicleCosts(xmlData : String) : ArrayList<HashMap<String, String>> {
        var empDataHashMap = HashMap<String, String>()
        var empList: ArrayList<HashMap<String, String>> = ArrayList()
        try {
            val builderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = builderFactory.newDocumentBuilder()
            val doc = docBuilder.parse(InputSource(StringReader(xmlData)))
            //reading the tag "employee" of empdetail file
            val AfschrijvingEnRente = doc.getElementsByTagName("")
            val ReparatieEnOnderhoud = doc.getElementsByTagName("ReparatieEnOnderhoud")
            val Banden = doc.getElementsByTagName("Banden")
            val MRB = doc.getElementsByTagName("MRB")
            val Verzekering = doc.getElementsByTagName("Verzekering")
            val TCO_Totaal = doc.getElementsByTagName("TCO_Totaal")

            empDataHashMap = HashMap()
            val parentEl = doc.getElementsByTagName("TCO_Uitkomst").item(0) as Element
            empDataHashMap.put("AfschrijvingEnRente", getNodeValue("AfschrijvingEnRente", parentEl))
            empDataHashMap.put("ReparatieEnOnderhoud", getNodeValue("ReparatieEnOnderhoud", parentEl))
            empDataHashMap.put("Banden", getNodeValue("Banden", parentEl))
            empDataHashMap.put("MRB", getNodeValue("MRB", parentEl))
            empDataHashMap.put("Verzekering", getNodeValue("Verzekering", parentEl))
            empDataHashMap.put("TCO_Totaal", getNodeValue("TCO_Totaal", parentEl))


            empList.add(empDataHashMap)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
        return empList
    }

    protected fun getNodeValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if (node != null) {
            if (node.hasChildNodes()) {
                val child = node.getFirstChild()
                while (child != null) {
                    if (child.getNodeType() === Node.TEXT_NODE) {
                        return child.getNodeValue()
                    }
                }
            }
        }
        return ""
    }

    private fun loadVehicleData(licenseplate : String, textView : TextView) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var loading = findViewById<RelativeLayout>(R.id.loader)
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
                if(car != null && fueldata != null) {
                    var catalogusprijs = "Onbekend"
                    if(car["catalogusprijs"] != null){
                        catalogusprijs = "€" + car["catalogusprijs"]
                    }
                    var vermogen = "Onbekend"
                    if(fueldata["nettomaximumvermogen"] != null){
                        vermogen = fueldata["nettomaximumvermogen"]?.toFloat()?.roundToInt().toString() + " kW / " + (fueldata["nettomaximumvermogen"]!!.toFloat() * 1.362).roundToInt().toString() + " pk"
                    }
                    var verbruik = "Onbekend"
                    if(fueldata["brandstofverbruik_gecombineerd"] != null) {
                        verbruik = (fueldata["brandstofverbruik_gecombineerd"]?.toFloat()).toString() + "l/100km"
                    }
                    var generalText = "\n\n" +
                            car["uitvoering"] +
                            "\n" + car["eerste_kleur"]

                    var vehicleText = formatAPKDate(car["vervaldatum_apk"]) +
                            "\n" + vermogen +
                            "\n" + car["massa_ledig_voertuig"] +
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
                    //set APK date text
//                    var textViewAPK = findViewById<TextView>(R.id.textViewAPK)
//                    textViewAPK.text = formatAPKDate(car["vervaldatum_apk"])

//                    //Set energylabel text
//                    if(car["zuinigheidslabel"] != null) {
//                        var textViewEnergy = findViewById<TextView>(R.id.textViewEnergy)
//                        textViewEnergy.text = "Energie " + car["zuinigheidslabel"]
//                    }
//
//                    //Set doors text
//                    if(car["aantal_deuren"] != null) {
//                        var textViewDoors = findViewById<TextView>(R.id.textViewDoors)
//                        textViewDoors.text = car["aantal_deuren"] + " deuren"
//                    }
//
//                    //Set body text
//                    var textViewBody = findViewById<TextView>(R.id.textViewBody)
//                    textViewBody.text = car["inrichting"]?.toLowerCase()?.capitalize()
//
//                    //set fuel text
//                    var textViewFuel = findViewById<TextView>(R.id.textViewFuel)
//                    textViewFuel.text = fueldata["brandstof_omschrijving"]?.toLowerCase()?.capitalize()
//                    if(fueldata["brandstof_omschrijving"]!! == "Elektriciteit"){
//                        var imageViewFuel = findViewById<ImageView>(R.id.FuelIcon)
//                        imageViewFuel.setImageResource(R.drawable.ic_power_plug)
//                    }
//
//                    //set mileage text
//                    if(fueldata["brandstofverbruik_gecombineerd"] != null) {
//                        var textViewMileage = findViewById<TextView>(R.id.textViewMileage)
//                        textViewMileage.text = (fueldata["brandstofverbruik_gecombineerd"]?.toFloat()).toString() + "l/100km"
//                    }
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
    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
}
