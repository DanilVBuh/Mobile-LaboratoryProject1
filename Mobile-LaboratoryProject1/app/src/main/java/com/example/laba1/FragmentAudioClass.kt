package com.example.laba1

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view_layout.*
import java.io.File

class FragmentAudioClass : Fragment(){
    private var fullpath = File(Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC")
    private var fileList: ArrayList<File> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        audioReaderNew(fullpath)



    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recycler_view_layout, container, false)

        val rv = rootView.findViewById(R.id.list_recycler_view) as RecyclerView
        rv.layoutManager = LinearLayoutManager(this!!.activity)
        rv.adapter = ArrayAdapter(fileList)

        val swipeHandler = object : SwipeToDeleteCallback(this!!.context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv.adapter as com.example.laba1.ArrayAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rv)

        return rootView
    }

    private fun audioReaderNew(root: File): ArrayList<File> {
        val listAllFiles = root.listFiles()


        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".mp3")) {
                    fileList.add(currentFile.absoluteFile)
                }
            }
        }

        return fileList
    }



}