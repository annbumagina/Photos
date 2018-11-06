package com.example.ann.photos

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_image_detail.*

class ImageDetailFragment: Fragment() {

    private lateinit var desc: String
    private lateinit var link: String
    private var i: Int = 0
    var binder: Loader2.MyBinder? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binder = service as Loader2.MyBinder
            binder!!.setCallback { p -> imageDetailView.setImageBitmap(p) }
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val strtext = arguments!!.getString("edttext")
        desc = arguments!!.getString(EXTRA_DESC)
        link = arguments!!.getString(EXTRA_URL)
        i = arguments!!.getInt(EXTRA_ID)
        return inflater.inflate(R.layout.activity_image_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textView2.text = desc
        val intent2 = Intent(context, Loader2::class.java)
        intent2.putExtra(EXTRA_ID, i)
        intent2.putExtra(EXTRA_URL, link)
        context!!.bindService(intent2, serviceConnection, Context.BIND_AUTO_CREATE)
        context!!.startService(intent2)
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unbindService(serviceConnection)
    }
}