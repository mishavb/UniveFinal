package com.example.univefinal

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_hypotheek_check.*
import kotlinx.android.synthetic.main.activity_hypotheek_check.toolbar
import kotlinx.android.synthetic.main.activity_zorg_check.*

class HypotheekCheck : AppCompatActivity() {
    private lateinit var webView: WebView
    private var loadCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hypotheek_check)
        setSupportActionBar(toolbar)


        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Bereken uw hypotheek"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.parseColor("#505050"))

        webView = findViewById(R.id.zorgCheckView)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.defaultTextEncodingName = "utf-8"

        webView.loadUrl("https://www.unive.nl/hypotheek/")

        var loader = findViewById<RelativeLayout>(R.id.loader)
        loader.visibility = View.VISIBLE
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)

                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                view?.loadUrl(
                    "javascript:(function() { " +
                            "var head = document.getElementsByClassName('mainHeader')[0].style.display='none'; " +
                            "var foot = document.getElementsByClassName('mainFooter')[0].style.display='none'; " +
                            "})()"
                )
                loader.visibility = View.INVISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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


}
