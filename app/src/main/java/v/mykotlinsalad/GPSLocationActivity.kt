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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_gpslocation.*
import org.json.JSONObject

class GPSLocationActivity : AppCompatActivity(), OnMapReadyCallback {



    var myDb = databaseHelper(this, null)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val apiKey = "bff78d1e25c341ad884080598125bf67"
    var lon: Double = 0.0
    var lat: Double = 0.0

    var cityName: String? = ""
    var country: String? = ""
    val startingPoint = LatLng(65.009009009009, 25.50388924350585)

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("onMapRedu", "Function start")

        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 5.5.toFloat()))

        // HAETAAN KAIKKI LOKAATION KANNASTA
        val cursor = myDb.getAllCoordinates()
        cursor!!.moveToFirst()
        Log.i("GPS OnCreate", "Coordinates: ")
        while(cursor.moveToNext()){
            var response = "LATITUDE: " + cursor.getString(1)+", LONGITUDE: "+ cursor.getString(2)+" CITY: "+ cursor.getString(3).toString()+" COUNTRY: "+cursor.getString(4).toString()
            Log.i("GPS OnCreate", response)
            var responseLocation = LatLng(cursor.getString(1).toDouble(),cursor.getString(2).toDouble())
            Log.i("GPS OnCreate End", responseLocation.toString())
            mMap.addMarker(MarkerOptions().position(responseLocation).title("Visited in "+cursor.getString(3)+", "+cursor.getString(4)))

        }

        // ASETETAAN LISTENERI LONGPRESSEILLE
        mMap.setOnMapLongClickListener(GoogleMap.OnMapLongClickListener { coordinates: LatLng ->
            Log.i("GPS OnCreate End", "Coordinates: "+coordinates)


            // Joka longpressillä tehdään api call. tämä vain testaus mielessä
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnCompleteListener {
                val queue = Volley.newRequestQueue(this)
                val url =
                    "https://api.openweathermap.org/data/2.5/weather?lat=" + coordinates.latitude.toString() + "&lon=" + coordinates.longitude.toString() + "&appid=" + apiKey
                val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

                    cityName = JSONObject(response).getString("name").toString()
                    //gpsDescription.text = JSONObject(response).toString()
                    country = JSONObject(response).getJSONObject("sys").getString("country").toString()

                }, Response.ErrorListener { Log.e("GPS", "Error in loading old locations") })
                queue.add(stringRequest)
                var data =
                    Coordinates(coordinates.longitude, coordinates.latitude, cityName.toString(), country.toString())
                myDb.insertCoordinates(data)
            }
            mMap.addMarker(MarkerOptions().position(coordinates).title("Visited in "+cityName.toString()+", "+country.toString()))
            Log.i("GPS OnCreate", "Data saved to database" )
        })
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpslocation)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView2) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    @SuppressLint("MissingPermission")
    fun search(view: View){

        Log.i("SearchButton","Search pressed")
        searchGPSlocationButton.isClickable= false
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
        fusedLocationClient.lastLocation.addOnCompleteListener {
            // get the city name
            val queue = Volley.newRequestQueue(this)
            val url =
                "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey

            Log.d("Lon form GPSLocation", lon.toString())
            Log.d("Lat form GPSLocation", lat.toString())
            val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

                cityName = JSONObject(response).getString("name").toString()
                //gpsDescription.text = JSONObject(response).toString()
                country = JSONObject(response).getJSONObject("sys").getString("country").toString()

                Log.i("Location City: ", cityName)
                Log.i("Location Country: ", country)
                val NewMarker = LatLng(lat,lon)
                mMap.addMarker(MarkerOptions().position(NewMarker).title("Visited in "+cityName.toString()+", "+country.toString()))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(NewMarker))

            }, Response.ErrorListener { Log.e("GPS", "Error in GPSLocationActivity") })
            queue.add(stringRequest)


            val data = Coordinates(lon,lat,cityName.toString(), country.toString())
            myDb.insertCoordinates(data)
            gpsTitle.text = (lon.toString()+", "+lat.toString()+" "+cityName.toString()+" "+country.toString())

        }
        searchGPSlocationButton.isClickable= true
    }

    fun clearDb(view: View){
        myDb.deleteAllCoordinates()
        mMap.clear()
    }
}



