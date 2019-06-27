package v.mykotlinsalad

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forecast.*
import org.json.JSONObject


class forecastActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val apiKey : String = "bff78d1e25c341ad884080598125bf67"
        // get json
        getJson(apiKey, "Oulu")
    }

    fun updateLocation(view: View){
        val newCity = new_city_text.text.toString()
        location.text = ""
        temperature.text = ""
        humidity.text = ""
        status.text = ""
        val apiKey = "bff78d1e25c341ad884080598125bf67"
        getJson(apiKey,newCity)
    }


    protected fun getJson(apiKey: Any?, targetLocation: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?q="+targetLocation+"&appid="+apiKey
        println("Used url: "+url)
        println("target location: "+ targetLocation)

        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

            // get location:
            val mainObject = JSONObject(response)
            println(mainObject.toString())
            val location_string = mainObject.getString("name")
            println("Location: "+location_string)
            location.text = location_string.toString()

            // get temperature
            val temp_result = JSONObject(response).getJSONObject("main")
            val temp_kelvin = temp_result.getString("temp").toDouble()
            println("faren: "+temp_kelvin)
            val temp_cels = (temp_kelvin - 273.15)
            temperature.text = (temp_cels.toString()).substring(0,4)+" C"+176.toChar()

            // get humidity
            val humidity_string = temp_result.getString("humidity").toFloat()
            println("humidity: "+ humidity_string)
            humidity.text = (humidity_string.toString()+"%")

            // get status
            val weather_status =  JSONObject(response).getJSONArray("weather").getJSONObject(0).getString("description")

            status.text = weather_status
        },
            Response.ErrorListener { temperature.text = "That didn't work!" })
        queue.add(stringRequest)
    }


    fun close(view: View){
        finish()
    }
}


