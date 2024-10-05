package myst.mostafayellowstone.timewiselearn.data.converter

import androidx.room.TypeConverter

class ColorListConverter {
    @TypeConverter
    fun fromColorList(colorList: List<Int>): String{
        return colorList.joinToString(","){it.toString()}
    }
    @TypeConverter
    fun toColorList(colorListString: String): List<Int>{
        return colorListString.split(",").map { it.toInt() }
    }
}