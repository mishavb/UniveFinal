package com.example.univefinal

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import com.example.univefinal.AppMethods.Companion.isConnectedToNetwork
import com.example.univefinal.AppMethods.Companion.isOnline


class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        if(!isOnline(this)){
            val intent = Intent(this, NetworkError::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

//        webView.loadUrl("https://www.unive.nl/hypotheek/")
//        webView.loadUrl("https://www.unive.nl/schadeservice/")
//        webView.loadUrl("https://www.unive.nl/autoverzekering/tips-tweedehands-auto-kopen")
//        webView.loadUrl("https://www.unive.nl/zorgverzekering/zorgcheck/")

        //privatelease??

//        intent.putExtra("webviewTitle", "Bereken uw hypotheek")
//        intent.putExtra("webviewTitle", "Zorgverzekering")
//        intent.putExtra("webviewTitle", "Schadehersteller")
//        intent.putExtra("webviewTitle", "Zorgverzekering")

        //button actions
        val buyCar = findViewById<Button>(R.id.buyCar)
        buyCar.setOnClickListener{
            val intent = Intent(this, VehicleMenu::class.java)
            startActivity(intent)
        }

        val buyHouse = findViewById<Button>(R.id.buyHouse)
        buyHouse.setOnClickListener{
            val intent = Intent(this, WebViewController::class.java)
            intent.putExtra("webviewTitle", "Bereken uw hypotheek")
            intent.putExtra("webviewUrl", "https://www.unive.nl/hypotheek/")
            startActivity(intent)
        }

        val damageService = findViewById<Button>(R.id.damageService)
        damageService.setOnClickListener{
            val intent = Intent(this, WebViewController::class.java)
            intent.putExtra("webviewTitle", "Schadehersteller")
            intent.putExtra("webviewUrl", "https://www.unive.nl/schadeservice/")
            startActivity(intent)
        }

        val healthInsurance = findViewById<Button>(R.id.healthInsurance)
        healthInsurance.setOnClickListener{
            val intent = Intent(this, WebViewController::class.java)
            intent.putExtra("webviewTitle", "Zorgverzekering")
            intent.putExtra("webviewUrl", "https://www.unive.nl/zorgverzekering/zorgcheck/")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

//        AppMethods.returnToMainMenu(id, this) //disable on main menu

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
