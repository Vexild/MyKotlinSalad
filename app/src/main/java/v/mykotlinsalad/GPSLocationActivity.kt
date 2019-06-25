package v.mykotlinsalad

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_gpslocation.*
import org.json.JSONObject

class GPSLocationActivity : AppCompatActivity() {

    var myDb = databaseHelper(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val apiKey = "bff78d1e25c341ad884080598125bf67"
    var lon: Double = 0.0
    var lat: Double = 0.0

    var cityName: String? = ""
    var country: String? = ""

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpslocation)

        // create local db if none exists and CREATE_TABLE = "CREATE TABLE $LOCATIONS ($ID Integer PRIMARY KEY AUTOINCREMENT, $LON, $LAT, $CITY, $COUNTRY)"


    }
    @SuppressLint("MissingPermission")
    fun search(view: View){
        // CREATE PERMISSION FOR USE OF GPS
        // in current build, you need to allow myKotlinSalad use GPS manually (settings -> search myKotlinSalad -> permissions -> gps toggle on)

        // create all mandatory things for GPS location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->


            if (location != null) {
                lon = location.longitude.toDouble()
            }
            if (location != null) {
                lat = location.latitude.toDouble()
            }
        }
        // get the city name
        while (lat != 0.0 && lon != 0.0) {
            val queue = Volley.newRequestQueue(this)
            val url =
                "https://api.openweathermap.org/data/2.5/weather?lat=" + lat.toInt() + "&lon=" + lon.toInt() + "&appid=" + apiKey


            Log.d("Lon form GPSLocation", lon.toString())
            Log.d("Lat form GPSLocation", lat.toString())
            val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

                cityName = JSONObject(response).getString("name").toString()
                gpsDescription.text = JSONObject(response).toString()
                country = Log.i("JSON", JSONObject(response).getJSONObject("sys").getString("country").toString()).toString()

                Log.i("Location City: ", cityName)
                Log.i("Location Country: ", country)
            }, Response.ErrorListener { Log.e("GPS", "That didn't work!") })
            queue.add(stringRequest)

            break
        }

        // take lon and lat and insert them into DB in form (id, lon, lat, city)
        myDb.insertCoordinates(lon,lat,cityName.toString(), country.toString() )
        gpsDescription.text = (myDb.getAllCoordinates()).toString()
        // display all points from db to map


        }
}

