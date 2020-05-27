package com.example.laba1

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AudioViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_audio_layout, parent, false)) {
    private var audioName: TextView? = null


    init {
        audioName = itemView.findViewById(R.id.audioName)
    }

    fun bind(audio: File) {
        audioName?.text = audio.name
    }
}