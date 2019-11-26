package com.example.univefinal

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_license_error.*

import kotlinx.android.synthetic.main.activity_network_error.*
import kotlinx.android.synthetic.main.activity_network_error.toolbar

class NetworkError : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_error)
        setSupportActionBar(toolbar)



        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(false)
        actionbar.setDisplayHomeAsUpEnabled(false)
        toolbar.setTitleTextColor(Color.BLACK)

        val tryAgain = findViewById<Button>(R.id.tryAgain)
        tryAgain.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
