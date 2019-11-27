package com.example.univefinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_license_error.*

import kotlinx.android.synthetic.main.activity_network_error.*
import kotlinx.android.synthetic.main.activity_network_error.toolbar
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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
            if(AppMethods.isOnline(this)){
                val intent = Intent(this, MainActivity::class.java)

                //start new activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("EXIT", true)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onBackPressed() {
        return
    }

}
