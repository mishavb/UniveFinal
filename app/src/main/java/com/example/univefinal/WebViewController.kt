package com.example.univefinal

import android.graphics.Bitmap
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
import kotlinx.android.synthetic.main.activity_main.*


class WebViewController : AppCompatActivity() {
    private lateinit var webView: WebView
    private var loadCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_controller)
        setSupportActionBar(toolbar)

        //get webview title & url
        val viewTitle = intent.getStringExtra("webviewTitle")
        val viewUrl = intent.getStringExtra("webviewUrl")

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = viewTitle
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(Color.parseColor("#505050"))

        if(viewUrl != null) {
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

            webView.loadUrl(viewUrl)

            var loader = findViewById<RelativeLayout>(R.id.loader)
            loader.visibility = View.VISIBLE
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)

                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    loader.visibility = View.INVISIBLE
                }

                override fun onPageFinished(view: WebView, url: String) {
                    //Strip header & footer
//                    view?.loadUrl(
//                        "javascript:(function() { " +
//                                "var head = document.getElementsByClassName('mainHeader')[0].style.display='none'; " +
//                                "var foot = document.getElementsByClassName('mainFooter')[0].style.display='none'; " +
//                                "})()"
//                    )
                }
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
