package v.mykotlinsalad

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        val DATABASE_NAME = "locationDB.db"
        val TABLE_NAME = "location"
        val ID = "ID"
        val LON = "lon"
        val LAT = "lat"
        val CITY = "city"
        val COUNTRY = "country"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        //db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME(ID INTEGER PRIMARY KEY AUTOINCREMENT, LON DOUBLE, LAT DOUBLE, CITY TEXT, COUNTRY TEXT)")
        val CREATE_PRODUCTS_TABLE = ( "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, lon DOUBLE, lat DOUBLE, city TEXT, country TEXT)" )
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME)
        onCreate(db)
    }

    fun insertCoordinates(coordinate: Coordinates){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(LON, coordinate.lon)
        cv.put(LAT, coordinate.lat)
        cv.put(CITY, coordinate.city)
        cv.put(COUNTRY, coordinate.country)
        db.insert(TABLE_NAME,null, cv)
        db.close()
    }

    fun getAllCoordinates(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun deleteAllCoordinates(): Int {
        val db = this.writableDatabase
        return db.delete("$TABLE_NAME",null,null)
        //return db.rawQuery ("DELETE FROM $TABLE_NAME", null)
    }

}

class Coordinates {
    var lat: Double? = null
    var lon: Double? = null
    var city: String? = null
    var country: String? = null
    constructor(lat: Double, lon: Double, city: String, country: String) {
        this.lat = lat
        this.lon = lon
        this.city = city
        this.country = country
    }
}