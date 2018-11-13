package com.example.ann.photos

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.util.Pair
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.LinkedList

class Loader : IntentService("Loader") {

    private val main = Handler(Looper.getMainLooper())
    private lateinit var callback: (JSONObject) -> Unit

    private val LOG_TAG = Loader::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "MyService onCreate")
    }

    override fun onHandleIntent(intent: Intent) {
        Log.d(LOG_TAG, "onHandleIntent: ")
        val path = intent.getStringExtra(EXTRA_URL)
        val url = URL(path)
        val connection = url.openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()
        val reader = BufferedReader(InputStreamReader(inputStream), 8)
        val sb = StringBuilder()
        var line = reader.readLine()
        while (line != null) {
            sb.append(line + "\n")
            line = reader.readLine()
        }
        val result = sb.substring(15, sb.length - 2)
        val jObject = JSONObject(result)
        main.post { callback(jObject) }
        inputStream.close()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(LOG_TAG, "onBind: ")
        return MyBinder(this)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(LOG_TAG, "onUnbind: ")
        callback = {_ -> }
        return super.onUnbind(intent)
    }

    class MyBinder(val service: Loader) : Binder() {
        fun setCallback(callback: (JSONObject) -> Unit) {
            service.callback = callback
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy Loader: ")
        super.onDestroy()
    }
}
