package com.example.stuplan.sqlite

import android.provider.BaseColumns

object CityContract {

    object CityEntry : BaseColumns {
        const val TABLE_NAME = "city"
        const val COLUMN_NAME = "city_name"
        const val COLUMN_TEMP = "city_temp"
    }
}