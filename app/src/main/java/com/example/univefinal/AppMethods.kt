package com.example.univefinal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import kotlinx.android.synthetic.main.activity_license_plate_manual.*
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.univefinal.AppMethods.Companion.isConnectedToNetwork
import org.w3c.dom.Text
import java.net.URL
import android.R.transition.explode
import android.R.array
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException


class AppMethods {
    companion object {
        // The next line should be the first statement in the file

        fun Context.isConnectedToNetwork(): Boolean {
            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
        }

        fun validLicensePlate(licensePlate : String) : Int
        {
            var found = 0
            if(licensePlate !== "") {
                val LP = licensePlate.replace("-", "").replace("-", "")
                val regexArray = arrayOf(
                    Regex("^([A-Z]{2})(\\d{2})(\\d{2})$"),  // 1     XX-99-99    (since 1951)
                    Regex("^(\\d{2})(\\d{2})([A-Z]{2})$"), // 2     99-99-XX    (since 1965)
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
                    Regex("^(\\d{3})([A-Z]{2})(\\d{1})$")
                )  // 14    999-XX-9

                var i = 1
                for (regexp in regexArray) {
                    if (LP.matches(regexp)) {
                        found = i
                    }
                    i += 1
                }
            }

            return found
        }

        fun returnToMainMenu(id : Int, context : Context) {
            if (id == R.id.to_main_menu) {
                val intent = Intent(context, MainActivity::class.java)

                //return to MainActivity
                context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

            }
        }

        fun formatLicenseForDisplay(pos : Int, licensePlate : String) : String{
            val LP = licensePlate.replace("-", "").replace("-", "")
            val builder = StringBuilder()
            when(pos) {
                1, 2, 3, 4, 5, 6 -> builder.append(LP.substring(0,2))
                    .append("-")
                    .append(LP.substring(2,4))
                    .append("-")
                    .append(LP.substring(4,6))
                7, 9 -> builder.append(LP.substring(0,2))
                    .append("-")
                    .append(LP.substring(2,5))
                    .append("-")
                    .append(LP.substring(5, 6))
                8, 10 -> builder.append(LP.substring(0,1))
                    .append("-")
                    .append(LP.substring(1,4))
                    .append("-")
                    .append(LP.substring(4,6))
                12, 13 -> builder.append(LP.substring(0,1))
                    .append("-")
                    .append(LP.substring(1,3))
                    .append("-")
                    .append(LP.substring(3,6))
                11, 14 -> builder.append(LP.substring(0,3))
                    .append("-")
                    .append(LP.substring(3,5))
                    .append("-")
                    .append(LP.substring(5,6))
            }
            return builder.toString()
        }

        fun isOnline(context : Context) : Boolean{
            return context.isConnectedToNetwork()
        }

        fun getJsonFromURL(wantedURL: String) : String {
            var text = ""
            if(wantedURL != "") {
                text = URL(wantedURL).readText()
            }
            return text
        }

        fun parseVehicleCosts(xmlData : String) : ArrayList<HashMap<String, String>> {
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

        fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
    }
}