package com.example.organize3.data.folderWithNotes

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Gson().fromJson(value, Array<String>::class.java).toList()
    }

    @TypeConverter
    fun fromList(value: List<String>?): String {
        return Gson().toJson(value)
    }
}