package com.example.ann.photos

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_image_detail.*
import android.support.v4.app.NavUtils
import android.view.MenuItem

const val EXTRA_ID = "com.example.ann.a.extra.ID"

class ImageDetailActivity: AppCompatActivity() {

    private val LOG_TAG = ImageDetailActivity::class.java.simpleName

    var binder: Loader2.MyBinder? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binder = service as Loader2.MyBinder
            binder!!.setCallback { p -> imageDetailView.setImageBitmap(p) }
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onCreate Image Detail: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)
        if (intent.getStringExtra(EXTRA_DESC) != null) {
            textView2.text = intent.getStringExtra(EXTRA_DESC)
            val intent2 = Intent(this, Loader2::class.java)
            intent2.putExtra(EXTRA_ID, intent.getIntExtra(EXTRA_ID, 0))
            intent2.putExtra(EXTRA_URL, intent.getStringExtra(EXTRA_URL))
            bindService(intent2, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent2)
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: ")
        super.onDestroy()
        unbindService(serviceConnection)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            val intent = NavUtils.getParentActivityIntent(this)
            intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            NavUtils.navigateUpTo(this, intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}