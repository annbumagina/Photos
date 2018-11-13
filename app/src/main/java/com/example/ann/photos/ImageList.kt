package com.example.ann.photos

import android.app.Activity
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_image_detail.*
import kotlinx.android.synthetic.main.image_list.*
import org.json.JSONObject

const val EXTRA_URL = "com.example.ann.a.extra.URL"
const val EXTRA_DESC = "com.example.ann.a.extra.DESC"
const val EXTRA_LIST = "com.example.ann.a.extra.LIST"

class ImageList: Fragment() {
    private val LOG_TAG = ImageList::class.java.simpleName
    var title: Array<String?> = arrayOfNulls(20)
    var links: Array<String?> = arrayOfNulls(20)
    var have = false
    var description: Array<String?> = arrayOfNulls(20)
    var bind = false
    val onClick = { id: Int ->
        if (activity?.findViewById<View>(R.id.fragment_content) != null) {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            bundle.putString(EXTRA_URL, links[id])
            bundle.putString(EXTRA_DESC, description[id])
            val fragobj = ImageDetailFragment()
            fragobj.setArguments(bundle)

            val transaction = getFragmentManager()!!.beginTransaction()
            transaction.replace(R.id.fragment_content, fragobj)
            transaction.commit()
        } else {
            val intent = Intent(context, ImageDetailActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_URL, links[id])
            intent.putExtra(EXTRA_DESC, description[id])
            startActivity(intent)
        }
    }
    var binder: Loader.MyBinder? = null
    var adapter: ListAdapter = ListAdapter(onClick, title)
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binder = service as Loader.MyBinder
            binder!!.setCallback { p -> setJSON(p) }
            bind = true
        }

        override fun onServiceDisconnected(name: ComponentName) { bind = false }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(LOG_TAG, "onCreateView: " + {if (savedInstanceState == null) "null" else "saved"}())
        return inflater.inflate(R.layout.image_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated: ")
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = adapter
        if (savedInstanceState == null || savedInstanceState.getStringArray(EXTRA_LIST) == null) {
            val intent = Intent(context, Loader::class.java)
            intent.putExtra(EXTRA_URL, "https://api.flickr.com/services/feeds/photos_public.gne?tags=people&tagmode=any&per_page=20&format=json")
            context!!.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            context!!.startService(intent)
        } else {
            title = savedInstanceState.getStringArray(EXTRA_LIST)
            links = savedInstanceState.getStringArray(EXTRA_URL)
            description = savedInstanceState.getStringArray(EXTRA_DESC)
            for (i in 0 .. title.size - 1) {
                adapter.setElement(i, title[i]!!)
            }
            have = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (have) {
            outState.putStringArray(EXTRA_LIST, title)
            outState.putStringArray(EXTRA_URL, links)
            outState.putStringArray(EXTRA_DESC, description)
        }
        Log.d(LOG_TAG, "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    fun setJSON(jObject: JSONObject) {
        val jArray = jObject.getJSONArray("items")
        for (i in 0 .. jArray.length() - 1) {
            val oneObject = jArray.getJSONObject(i)
            val object2 = oneObject.getJSONObject("media")
            val aJsonString = object2.getString("m")
            links[i] = aJsonString.substring(0, aJsonString.length - 5) + "c.jpg"
            description[i] = oneObject.getString("tags")
            title[i] = oneObject.getString("title")
            adapter.setElement(i, title[i]!!)
        }
        have = true
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: ")
        super.onDestroy()
        if (bind) {
            context!!.unbindService(serviceConnection)
        }
    }

    override fun onResume() {
        Log.d(LOG_TAG, "onResume: ")
        super.onResume()
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop: ")
        super.onStop()
    }
}