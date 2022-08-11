package com.example.stuplan.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.weathometer.model.CitySqlite


class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableCity =
            "CREATE TABLE ${CityContract.CityEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${CityContract.CityEntry.COLUMN_NAME} TEXT ,"+
                    "${CityContract.CityEntry.COLUMN_TEMP} TEXT )"

        db.execSQL(createTableCity)//SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        val dropCityTable = "DROP TABLE IF EXISTS ${CityContract.CityEntry.TABLE_NAME}"
        db.execSQL(dropCityTable) //SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertCity(city : CitySqlite) : Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(CityContract.CityEntry.COLUMN_NAME, city.name)
            put(CityContract.CityEntry.COLUMN_TEMP, city.temp)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(CityContract.CityEntry.TABLE_NAME, null, values)
        db.close()
        return newRowId
    }


    fun getAllCity(): ArrayList<CitySqlite>{
            val cityList = ArrayList<CitySqlite>()
            val selectAll = "SELECT * FROM ${CityContract.CityEntry.TABLE_NAME}"

            val db = this.readableDatabase

            // Define a projection that specifies which columns from the database you will actually use after this query.
            val projection =
                arrayOf(BaseColumns._ID,
                    CityContract.CityEntry.COLUMN_NAME)

            val cursor = db.query(
                CityContract.CityEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            with(cursor) {
                while (moveToNext()) {
                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val cityName = getString(this.getColumnIndexOrThrow(CityContract.CityEntry.COLUMN_NAME))
                    val cityTemp = getString(this.getColumnIndexOrThrow(CityContract.CityEntry.COLUMN_TEMP))
                    cityList.add(CitySqlite(id,cityName,cityTemp))
                }
            }
            cursor.close()
            db.close()

            return cityList

        }

    fun deleteCity(city: CitySqlite) {
        val db = this.writableDatabase

        // Define 'where' part of query.
        val selection = "${BaseColumns._ID} = ${city.id}"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(city.id)
        // Issue SQL statement.
        val deletedRows = db.delete(CityContract.CityEntry.TABLE_NAME, selection, null)
        db.close()
    }

    fun doesCityExist(ColumnData: String): Boolean {
        val q = "Select * FROM ${CityContract.CityEntry.TABLE_NAME} WHERE ${CityContract.CityEntry.COLUMN_NAME}='$ColumnData';"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(q, null)
        return cursor.moveToFirst()
    }

    fun clearAllData(){
        val db = this.writableDatabase
        db.delete(CityContract.CityEntry.TABLE_NAME, null, null)
        db.close()


    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 3
        const val DB_NAME = "Weathometer.db"
    }
}