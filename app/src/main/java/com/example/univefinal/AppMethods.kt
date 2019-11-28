package com.example.univefinal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import kotlinx.android.synthetic.main.activity_license_plate_manual.*
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.univefinal.AppMethods.Companion.isConnectedToNetwork


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
            if (context.isConnectedToNetwork()) {
                Log.d("----- Online -----", "yes")
                return true
            } else {
                // Show disconnected screen
                Log.d("----- Online -----", "no")
                return false
            }
        }

    }
}