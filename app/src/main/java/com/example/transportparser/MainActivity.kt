package com.example.transportparser

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.btn).setOnClickListener {
            Log.d(TAG, "onCreate: START SAVE")
            val logPath = this.applicationContext.getExternalFilesDir("logs").toString()+"/allTransport.json"
            val writer = FileWriter(logPath, true)
            val buffer = BufferedWriter(writer)
            buffer.write(Klaxon().toJsonString(AllTransport.allTransport))
            buffer.close()
            Log.d(TAG, "onCreate: SAVED")
        }
        runBlocking(Dispatchers.IO) {
            val allDoc: Document = Jsoup.connect("https://xn--24-jlcxbqgdssj.xn--p1ai/%D0%B4%D0%BE%D0%BD%D0%B5%D1%86%D0%BA").get()
            val allLi = allDoc.select("li")
            val allA = allLi.select("a")
            val allID = allA.textNodes()
            allID.forEachIndexed{ idIndex, id ->
                if(id.text().length <= 6){
                    val transportId = AllTransport.allTransport.size
                    val transportDoc: Document = Jsoup.connect(allA[idIndex].attr("href")).get()
                    val transportLi = transportDoc.select("li")
                    val tranportTuda = arrayListOf<String>()
                    val tranportObratno = arrayListOf<String>()

                    transportLi.forEach {
                        if(it.hasClass("map-lime"))
                            tranportTuda.add(it.select("span").first()?.text()?:"")
                        else
                            if(it.hasClass("map-red"))
                                tranportObratno.add(it.select("span").first()?.text()?:"")
                    }

                    val transportTypeName = transportDoc.select("h1").first().text()?.lowercase()?:""
                    val transportType =
                        if(transportTypeName.contains("автобус") || transportTypeName.contains("такси"))
                            TransportType.BUS
                        else
                            if(transportTypeName.contains("троллейбус"))
                                TransportType.TROLLEYBUS
                            else
                                TransportType.TRAM

                    AllTransport.allTransport.add(Transport(
                        transportId,
                        id.text(),
                        tranportTuda,
                        tranportObratno,
                        false,
                        transportType
                    ))
                }

                Log.d(TAG, "onCreate: ${AllTransport.allTransport.size}")
            }
            Log.d(TAG, "onCreate: DONEEE")
            val log = Klaxon().toJsonString(AllTransport.allTransport)
            Log.d(TAG, "onCreate: $log")
        }

    }
}