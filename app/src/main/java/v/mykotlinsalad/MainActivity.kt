package v.mykotlinsalad

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun openWeather(view: View){
        val intent = Intent(this, forecastActivity::class.java)
        startActivity(intent)
    }
    fun openLiveView(view: View){
        val intent = Intent(this, liveCamActivity::class.java)
        startActivity(intent)
    }
    fun openGPSLocation(view: View){
        val intent = Intent(this, GPSLocationActivity::class.java)
        startActivity(intent)
    }
}
