package com.example.univefinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
        if (supportActionBar != null)
            supportActionBar?.hide()
        //button actions
        val buyCar = findViewById<Button>(R.id.buyCar)
        buyCar.requestFocus()
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

}
