package com.example.ann.photos

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import android.os.HandlerThread
import android.util.LruCache


class Loader2 : Service() {

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private val main = Handler(Looper.getMainLooper())
    private lateinit var callback: (Bitmap) -> Unit
    //private var images: Array<Bitmap?> = arrayOfNulls(20)

    val cacheSize = 2 * 10 * 1024 * 1024; // 4MiB
    var images = object : LruCache<Int, Bitmap>(cacheSize) {
        override fun sizeOf(key: Int, bitmap: Bitmap): Int {
            return bitmap.byteCount
        }
    }

    private val LOG_TAG = Loader2::class.java.simpleName

    override fun onCreate() {
        Log.d(LOG_TAG, "onCreate: ")
        super.onCreate()

        mHandlerThread = HandlerThread("LocalServiceThread")
        mHandlerThread!!.start()
        mHandler = Handler(mHandlerThread!!.looper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand: " + intent!!.getIntExtra(EXTRA_ID, 0) + ' ' + intent.getStringExtra(EXTRA_URL))
        val id = intent.getIntExtra(EXTRA_ID, 0)
        var bmp = images.get(id)
        if (bmp == null) {
            val path = intent.getStringExtra(EXTRA_URL)
            mHandler?.post {
                val url = URL(path)
                url.openConnection()
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                main.post {
                    images.put(id, bmp)
                    callback(bmp)
                }
            }
        } else {
            main.post {
                callback(bmp)
            }
        }
        return super.onStartCommand(intent, flags, startId)
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

    class MyBinder(val service: Loader2) : Binder() {
        fun setCallback(callback: (Bitmap) -> Unit) {
            service.callback = callback
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy Loader2: ")
        super.onDestroy()
    }
}
