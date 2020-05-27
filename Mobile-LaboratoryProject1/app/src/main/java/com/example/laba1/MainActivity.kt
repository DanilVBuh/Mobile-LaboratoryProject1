package com.example.laba1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pause_sheet.*
import android.os.CountDownTimer
import android.os.Environment
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_audio_layout.*
import java.io.File
import java.io.IOException
import android.os.Build
import kotlinx.android.synthetic.main.launch_screen.*
import java.io.FileOutputStream
import java.io.PrintWriter


@SuppressLint("ByteOrderMark")
class MainActivity : AppCompatActivity() {

    private var audioFilePath: String? = null
    private var additionalTextPath: String? = null

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var isRecording: Boolean = false

    private var fileList: ArrayList<File> = ArrayList()


    private var fullpath = File(Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC")




    @SuppressLint("InflateParams", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNewFolder(getString(R.string.folder_name), Environment.getExternalStorageDirectory().absolutePath)
        createNewFolder("AdditionalTXT", Environment.getExternalStorageDirectory().absolutePath+"/VoiceREC")
        audioReaderNew(fullpath)



        audioFilePath = Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/recording.mp3"
//        additionalTextPath = Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/AdditionalTXT/additional.txt"
//        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/AdditionalTXT/","additional.txt")

        object : CountDownTimer(2000,1000){
            override fun onTick(millisUntilFinished: Long) {
                launch_screen_layout.visibility = View.VISIBLE
            }

            override fun onFinish() {
                launch_screen_layout.visibility = View.GONE
            }
        }.start()



        recordButton.setOnClickListener {
            mediaRecorder = MediaRecorder()

            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(audioFilePath)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)
            } else {
                pause_sheet_layout.visibility = View.VISIBLE
                startRecording()
                var timeLimit = 3600000L
                object : CountDownTimer(timeLimit, 10) {

                    override fun onTick(millisUntilFinished: Long) {
                        val time = timeLimit - millisUntilFinished
                        var millisec = ((time % 1000) / 10).toString()
                        var sec = ((time % 60000) / 1000).toString()
                        var min = (time / 60000).toString()
                        if (min.toInt() < 10) min = "0$min"
                        if (sec.toInt() < 10) sec = "0$sec"
                        if (millisec.toInt() < 10) millisec = "0$millisec"
                        timerView.text = min + ":" + sec + ":" + millisec
                        if (pauseButton.isPressed) {
                            cancel()
                        }
                    }

                    override fun onFinish() {
                        timerView.text = "TOO LONG AUDIO!"
                    }
                }.start()
                recordButton.isEnabled=false
            }
        }



        pauseButton.setOnClickListener{
            recordButton.isEnabled=true
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Save as")

            val dialogLayout = layoutInflater.inflate(R.layout.edit_text_gialog, null)
            val newEditText  = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)

            builder.setPositiveButton("SAVE"){dialog, which ->
                Toast.makeText(applicationContext,"Saved successfully",Toast.LENGTH_SHORT).show()
                pause_sheet_layout.visibility = View.GONE
                setFileName(newEditText.text.toString())
                val file = File(Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/AdditionalTXT",newEditText.text.toString()+".txt")
                //setTextFileName(newEditText.text.toString())

                val hintBuilder = AlertDialog.Builder(this)

                hintBuilder.setTitle("Input additional text")

                val hintDialogLayout = layoutInflater.inflate(R.layout.additional_text_dialog, null)
                val newHintEditText  = dialogLayout.findViewById<EditText>(R.id.editAdditionalText)
                hintBuilder.setView(hintDialogLayout)

                hintBuilder.setPositiveButton("SAVE"){dialog, which ->
                    Toast.makeText(applicationContext,"Additional text added",Toast.LENGTH_SHORT).show()
                    val file = File(Environment.getExternalStorageDirectory().absolutePath + "/VoiceREC/AdditionalTXT",newEditText.text.toString()+".txt")

                    FileOutputStream(file).use {
                        it.write("record goes here".toByteArray())
                    }
                    //file.appendText(newHintEditText.text.toString())


                }

                hintBuilder.setNegativeButton("CANCEL"){dialog,which ->
                    Toast.makeText(applicationContext,"Saved without additional text",Toast.LENGTH_SHORT).show()
                }

                val hintDialog: AlertDialog = hintBuilder.create()
                hintDialog.setCancelable(false)
                hintDialog.show()

            }

            builder.setNegativeButton("CANCEL"){dialog,which ->
                Toast.makeText(applicationContext,"Saving was canceled",Toast.LENGTH_SHORT).show()
                pause_sheet_layout.visibility = View.GONE

            }

            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()

            stopRecording()
        }


    }



    private fun createNewFolder(folderName: String, path: String) {
        val folderAlreadyExists = File(path).listFiles().map { it.name }.contains(folderName)
        if (!folderAlreadyExists) {
            val file = File(path, folderName)
            file.mkdir()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            isRecording = false
        }
    }

    private fun setFileName(text: String) {
        val dir = Environment.getExternalStorageDirectory()
        if (dir.exists()) {
            val from = File(dir, "VoiceREC/recording.mp3")
            val to = File(dir, "VoiceREC/$text.mp3")
            if (from.exists())
                from.renameTo(to)
        }
    }

    private fun setTextFileName(text: String) {
        val dir = Environment.getExternalStorageDirectory()
        if (dir.exists()) {
            val from = File(dir, "VoiceREC/AdditionalTXT/additional.txt")
            val to = File(dir, "VoiceREC/AdditionalTXT/$text.txt")
            if (from.exists())
                from.renameTo(to)
        }
    }

    private fun audioReaderNew(root: File) {
        val listAllFiles = root.listFiles()


        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".mp3")) {
                    fileList.add(currentFile.absoluteFile)
//                    txtView.text = txtView.text.toString()+ "   " + currentFile.name
                }
            }
        }
    }


}


