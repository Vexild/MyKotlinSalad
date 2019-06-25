package v.mykotlinsalad

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object{
        val DATABASE_NAME = "locationDB.db"
        val TABLE_NAME = "location"
        val ID = "ID"
        val LON = "LON"
        val LAT = "LAT"
        val CITY = "CITY"
        val COUNTRY = "COUNTRY"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME(ID INTEGER PRIMARY KEY AUTOINCREMENT, LON DOUBLE, LAT DOUBLE, CITY TEXT, COUNTRY TEXT)")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertCoordinates(lon: Double, lat: Double, city: String, country: String): Boolean?{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(LON, lon)
        cv.put(LAT, lat)
        cv.put(CITY, city)
        cv.put(COUNTRY, country)
        val res = db.insert(TABLE_NAME,null, cv)
        return !res.equals(-1)
    }

    fun getAllCoordinates(): Cursor{
        val db = this.writableDatabase
        return db.rawQuery("SELECT LON, LAT FROM $TABLE_NAME", null)
    }

    fun deleteCoordinates(id: String): Int? {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "ID =? ", arrayOf(id))
    }

}