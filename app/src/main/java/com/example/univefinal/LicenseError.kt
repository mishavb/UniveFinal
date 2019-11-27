package com.example.univefinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_license_error.*
import kotlinx.android.synthetic.main.activity_license_error.toolbar
import kotlinx.android.synthetic.main.activity_license_plate_manual.*

class LicenseError : AppCompatActivity() {
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
        setContentView(R.layout.activity_license_error)
        setSupportActionBar(toolbar)

        parentView = intent.getStringExtra("parentView")

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.BLACK)

        val tryAgain = findViewById<Button>(R.id.tryAgain)
        var backIntent = Intent()
        tryAgain.setOnClickListener{
            when(parentView){
                "scan" -> backIntent = Intent(this, LicensePlateScan::class.java)
                "manual" -> backIntent = Intent(this, LicensePlateManual::class.java)
            }

            startActivity(backIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
