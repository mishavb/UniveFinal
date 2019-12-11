package com.example.univefinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_vehicle_menu.*
import kotlinx.android.synthetic.main.activity_vehicle_menu.toolbar

class VehicleMenu : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if(!AppMethods.isOnline(this)){
            val intent = Intent(this, NetworkError::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_menu)
        setSupportActionBar(toolbar)


        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        //button actions
        val retrieveVehicleInfo = findViewById<Button>(R.id.retrieve_vehicle_info)
        retrieveVehicleInfo.setOnClickListener{
            val intent = Intent(this, LicensePlateManual::class.java)
            startActivity(intent)
        }

        val privateLease = findViewById<Button>(R.id.privateLease)
        privateLease.setOnClickListener{
            val intent = Intent(this, PrivateLease::class.java)
            startActivity(intent)
        }

        val tips = findViewById<Button>(R.id.tips)
        tips.setOnClickListener{
            val intent = Intent(this, WebViewController::class.java)
            intent.putExtra("webviewTitle", "Tips bij aankoop auto")
            intent.putExtra("webviewUrl", "https://www.unive.nl/autoverzekering/tips-tweedehands-auto-kopen")
            startActivity(intent)
        }

        val privateLease = findViewById<Button>(R.id.privateLease)
        privateLease.setOnClickListener{
            val intent = Intent(this, WebViewController::class.java)
            intent.putExtra("webviewTitle", "Private Lease")
            intent.putExtra("webviewUrl", "https://privatelease.beta.unive.nl/")
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

        AppMethods.returnToMainMenu(id, this)

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
