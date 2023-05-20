package com.musique.mpiano

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.musique.mpiano.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var soundManager: PianoSoundManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        soundManager = PianoSoundManager()

        val pianoView = binding.piano
        pianoView.onPianoClickListener = object : PianoView.OnPianoClickListener {
            override fun onWhiteKeyPressed(note: String) {
                // Handle white key press
                Log.e("PIANO", "note $note")
                soundManager.playSound(note)
            }

            override fun onBlackKeyPressed(note: String) {
                // Handle black key press
                Log.e("PIANO", "black note $note")
                soundManager.playSound(note)
            }

            override fun onKeyReleased() {
                Log.e("PIANO", "onKeyReleased")
                soundManager.stopSound()
            }
        }


    }

    override fun onDestroy() {
        soundManager.release()
        super.onDestroy()
    }
}