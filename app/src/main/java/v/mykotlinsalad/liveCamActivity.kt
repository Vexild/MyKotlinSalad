package v.mykotlinsalad

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_live_cam.*

class liveCamActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_cam)

        korkeasaariLiveView!!.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String? ): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        korkeasaariLiveView.settings.javaScriptEnabled = true
        korkeasaariLiveView.settings.loadWithOverviewMode = true
        korkeasaariLiveView.settings.useWideViewPort = true
        korkeasaariLiveView.settings.loadsImagesAutomatically = true
        korkeasaariLiveView.settings.allowUniversalAccessFromFileURLs = true
        korkeasaariLiveView!!.loadUrl("https://video.nest.com/embedded/live/e7d4ndr04w?autoplay=0")

    }
}

