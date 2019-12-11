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




class AppMethods {
    companion object {
        // The next line should be the first statement in the file

        fun Context.isConnectedToNetwork(): Boolean {
            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
        }

        fun validLicensePlate(licensePlate : String) : Int
        {
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
                Regex("^(\\d{3})([A-Z]{2})(\\d{1})$"))  // 14    999-XX-9

            var found = 0
            var i = 1
            for(regexp in regexArray) {
                if(LP.matches(regexp))
                {
                    found = i
                }
                i+=1
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

        fun BelastingRipper() : String {
            val belastingData = URL("https://www.belastingdienst.nl/common/js/iah/motorrijtuigenbelasting.js").readText()

            return belastingData
        }

        fun fetchAllProvinces() : String { //MutableMap<String, MutableMap<Int, String?>>
            val input = BelastingRipper()
            /*val startTag = "var dataNH=new Array(lenArray);"
            val endTag = "var dataPH_NH=new Array(lenArray);"
            var step1 = input.split(startTag)
            var step2 = step1[1].split(endTag)

            var replaceArray = listOf<String>(
                "var dataFR=new Array(lenArray);",
                "var dataUT=new Array(lenArray);",
                "var dataNB=new Array(lenArray);",
                "var dataLI=new Array(lenArray);",
                "var dataFL=new Array(lenArray);",
                "var dataOV=new Array(lenArray);",
                "var dataZL=new Array(lenArray);",
                "var dataGL=new Array(lenArray);",
                "var dataGR=new Array(lenArray);",
                "var dataZH=new Array(lenArray);",
                "var dataDR=new Array(lenArray);")

            var step3 = step2[0].replace(replaceArray[0], "<<END>>")
                .replace(replaceArray[1], "<<END>>")
                .replace(replaceArray[2], "<<END>>")
                .replace(replaceArray[3], "<<END>>")
                .replace(replaceArray[4], "<<END>>")
                .replace(replaceArray[5], "<<END>>")
                .replace(replaceArray[6], "<<END>>")
                .replace(replaceArray[7], "<<END>>")
                .replace(replaceArray[8], "<<END>>")
                .replace(replaceArray[9], "<<END>>")
                .replace(replaceArray[10], "<<END>>")
                .replace("lenArray=46;", "<<END>>")

            var step4 = step3.split("<<END>>") //province list
            var newList = mutableMapOf<String, List<String>>()
            for(provence in step4)
            {
                var provenceData = provence.split(",")
                var provenceSubstring = provenceData[2].substring(0, 6)

                var provenceKey = provenceSubstring.replace("data", "")
                var valueList = mutableListOf<String>()
                for(data in provenceData)
                {
                    valueList.add(data)
                }

                newList.put(provenceKey, mutableListOf("test"))
            }

            return step4.toString()
        }*/
            return input
        }
    }
}