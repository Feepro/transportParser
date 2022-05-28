package com.example.transportparser


enum class TransportType{
    BUS,
    TROLLEYBUS,
    TRAM
}

data class Transport(
    val id:Int? = null,
    val number:String? = null,
    val tuda: List<String>?,
    val obratno: List<String>?,
    var isFavorite:Boolean = false,
    val transportType:TransportType = TransportType.BUS
)