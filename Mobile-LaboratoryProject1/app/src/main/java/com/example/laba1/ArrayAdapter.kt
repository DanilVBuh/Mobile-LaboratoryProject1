package com.example.laba1

import android.media.MediaActionSound
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream

class ArrayAdapter(private val fileList: ArrayList<File>): RecyclerView.Adapter<ArrayAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var isNotPlaying: Boolean = true



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val audioName = itemView.findViewById<TextView>(R.id.audioName) as TextView
        val playButton = itemView.findViewById<ImageButton>(R.id.playAudioButton) as ImageButton
        val timeNow = itemView.findViewById<TextView>(R.id.timeNow) as TextView
        val duration = itemView.findViewById<TextView>(R.id.duration) as TextView
        val seekBar = itemView.findViewById<SeekBar>(R.id.seekBar) as SeekBar
        val additionalText = itemView.findViewById<TextView>(R.id.additionalTextInList) as TextView



    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val string = fileList[position].name
        val substring: String? = string.substringBefore(".mp3")

        val txtFile = Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/AdditionalTXT/" + substring + ".txt"
        val F =  File(txtFile)
        val inputAsString = FileInputStream(F).bufferedReader().use { it.readText() }

        holder.additionalText.text = inputAsString

        holder.audioName.text = substring
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(fileList[position].toString())
        val dur = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        val min = dur / 60000
        val sec =(dur % 60000) / 1000
        holder.duration.text=min.toString()+":"+sec.toString()

        holder.playButton.setOnClickListener {
            if (isNotPlaying) {
                playAudio(fileList[position].path)
                mediaPlayer
                isNotPlaying = false
                holder.playButton.setImageResource(R.drawable.ic_pause_circle_filled)

            } else {
                stopAudio()
                isNotPlaying = true
                holder.playButton.setImageResource(R.drawable.ic_play_circle_filled)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.fragment_audio_layout, parent, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    private fun playAudio(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun removeAt(position: Int) {
        fileList.removeAt(position)
        notifyItemRemoved(position)
        if(fileList[position].exists())
            fileList[position].delete()
    }

}