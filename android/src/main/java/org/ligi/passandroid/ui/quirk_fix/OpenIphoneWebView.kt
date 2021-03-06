package org.ligi.passandroid.ui.quirk_fix

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.passandroid.R
import org.ligi.passandroid.functions.IPHONE_USER_AGENT

class OpenIphoneWebView : Activity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data ?: return
        val webView = WebView(this)
        webView.settings.userAgentString = IPHONE_USER_AGENT

        webView.settings.javaScriptEnabled = true

        webView.loadUrl(data.toString())
        setContentView(webView)

        val backgroundColor = ContextCompat.getColor(this, R.color.dividing_color)
        val loadToast = LoadToast(this).setText("Loading").setBackgroundColor(backgroundColor).show()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loadToast.success()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                loadToast.error()
            }
        }

    }
}
