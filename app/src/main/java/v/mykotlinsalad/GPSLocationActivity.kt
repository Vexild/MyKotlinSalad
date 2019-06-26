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


    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("onMapRedu", "Function start")
        mMap = googleMap

        val startingPoint = LatLng(65.009009009009, 25.50388924350585)
        /*
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 5.5.toFloat()))
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpslocation)
        Log.i("onCreate In Maps", "Checkpoint1")
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView2) as SupportMapFragment
        Log.i("onCreate In Maps", "Checkpoint2")
        mapFragment.getMapAsync(this)
        // create local db if none exists and CREATE_TABLE = "CREATE TABLE $LOCATIONS ($ID Integer PRIMARY KEY AUTOINCREMENT, $LON, $LAT, $CITY, $COUNTRY)"

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
                mMap.addMarker(MarkerOptions().position(NewMarker).title("Marker in "+cityName.toString()+", "+country.toString()))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(NewMarker))

            }, Response.ErrorListener { Log.e("GPS", "Error in GPSLocationActivity") })
            queue.add(stringRequest)

            // display all points from db to map
            // Add a marker in Sydney and move the camera




            // take lon and lat and insert them into DB in form (id, lon, lat, city)7
            //myDb.deleteAllCoordinates()
            val data = Coordinates(lat,lon,cityName.toString(), country.toString())
            myDb.insertCoordinates(data)


            val cursor = myDb.getAllCoordinates()
            cursor!!.moveToFirst()
            //gpsDescription.append((cursor.getString(cursor.getColumnIndex(databaseHelper.ID))))
            while (cursor.moveToNext()) {
                myDb.deleteAllCoordinates()

               // Log.i("DATABASE"," LATTITUDE: " + cursor.getString(cursor.getColumnIndex(databaseHelper.ID)))
                //Log.i("DATABASE"," LONGITUDE: " + cursor.getString(cursor.getColumnIndex(databaseHelper.LON)))
                val response = "LATITUDE: " + cursor.getString(1)+", LONGITUDE: "+ cursor.getString(2)+" CITY: "+ cursor.getString(3).toString()+" COUNTRY: "+cursor.getString(4).toString()
                Log.i("DATABASE"," LATITUDE: " + cursor.getString(1)+", LONGITUDE: "+ cursor.getString(2)+" CITY: "+ cursor.getString(3).toString()+" COUNTRY: "+cursor.getString(4).toString())
                gpsTitle.text = response
            }
            cursor.close()
        }
        searchGPSlocationButton.isClickable= true
        }

}



