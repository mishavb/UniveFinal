package com.example.univefinal

import android.content.Context
import android.content.Intent

class AppMethods {
    companion object {
        fun validLicensePlate(licensePlate : String) : Int
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
    }
}