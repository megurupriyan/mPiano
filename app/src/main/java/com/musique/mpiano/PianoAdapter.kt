package com.musique.mpiano

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.musique.mpiano.databinding.PianoBlackKeyBinding
import com.musique.mpiano.databinding.PianoWhiteKeyBinding
import com.musique.mpiano.models.PianoKey

class PianoAdapter(val onPianoPlayed: (key: PianoKey) -> Unit, val stopSound: (key: PianoKey) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val notesFrequencies = mapOf(
//        "C0" to 16.35f,
//        "C#0" to 17.32f,
//        "D0" to 18.35f,
//        "D#0" to 19.45f,
//        "E0" to 20.60f,
//        "F0" to 21.83f,
//        "F#0" to 23.12f,
//        "G0" to 24.50f,
//        "G#0" to 25.96f,
//        "A0" to 27.50f,
//        "A#0" to 29.14f,
//        "B0" to 30.87f,
//        "C1" to 32.70f,
//        "C#1" to 34.65f,
//        "D1" to 36.71f,
//        "D#1" to 38.89f,
//        "E1" to 41.20f,
//        "F1" to 43.65f,
//        "F#1" to 46.25f,
//        "G1" to 49.00f,
//        "G#1" to 51.91f,
//        "A1" to 55.00f,
//        "A#1" to 58.27f,
//        "B1" to 61.74f,
//        "C2" to 65.41f,
//        "C#2" to 69.30f,
//        "D2" to 73.42f,
//        "D#2" to 77.78f,
//        "E2" to 82.41f,
//        "F2" to 87.31f,
//        "F#2" to 92.50f,
//        "G2" to 98.00f,
//        "G#2" to 103.83f,
//        "A2" to 110.00f,
//        "A#2" to 116.54f,
//        "B2" to 123.47f,
//        "C3" to 130.81f,
//        "C#3" to 138.59f,
//        "D3" to 146.83f,
//        "D#3" to 155.56f,
//        "E3" to 164.81f,
//        "F3" to 174.61f,
//        "F#3" to 185.00f,
//        "G3" to 196.00f,
//        "G#3" to 207.65f,
//        "A3" to 220.00f,
//        "A#3" to 233.08f,
//        "B3" to 246.94f,
        "C4" to 261.63f,
        "C#4" to 277.18f,
        "D4" to 293.66f,
        "D#4" to 311.13f,
        "E4" to 329.63f,
        "F4" to 349.23f,
        "F#4" to 369.99f,
        "G4" to 392.00f,
        "G#4" to 415.30f,
        "A4" to 440.00f,
        "A#4" to 466.16f,
        "B4" to 493.88f,
        "C5" to 523.25f,
        "C#5" to 554.37f,
        "D5" to 587.33f,
        "D#5" to 622.25f,
//        "E5" to 659.25f,
//        "F5" to 698.46f,
//        "F#5" to 739.99f,
//        "G5" to 783.99f,
//        "G#5" to 830.61f,
//        "A5" to 880.00f,
//        "A#5" to 932.33f,
//        "B5" to 987.77f,
//        "C6" to 1046.50f,
//        "C#6" to 1108.73f,
//        "D6" to 1174.66f,
//        "D#6" to 1244.51f,
//        "E6" to 1318.51f,
//        "F6" to 1396.91f,
//        "F#6" to 1479.98f,
//        "G6" to 1567.98f,
//        "G#6" to 1661.22f,
//        "A6" to 1760.00f,
//        "A#6" to 1864.66f,
//        "B6" to 1975.53f,
//        "C7" to 2093.00f
    )


    private var keys: List<PianoKey> = generateKeys()

    private fun generateKeys(): List<PianoKey> {
        val keyList = mutableListOf<PianoKey>()
        for (note in notesFrequencies.keys) {
            val isBlackKey = note.contains("#")
            val color = if (isBlackKey) Color.BLACK else Color.WHITE
            val frequency = notesFrequencies[note] ?: continue
            val key = PianoKey(note, color, isBlackKey, getOctaveFromNoteString(note), frequency)
            keyList.add(key)
        }
        return keyList
    }

    fun getOctaveFromNoteString(note: String): Int {
        val regex = "\\d+".toRegex()
        val matchResult = regex.find(note)
        return matchResult?.value?.toIntOrNull() ?: -1
    }

    init {
//        val keys =
        this.keys = generateKeys()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_WHITE_KEY -> {
                val binding = PianoWhiteKeyBinding.inflate(inflater, parent, false)
                WhiteKeyViewHolder(binding)
            }

            VIEW_TYPE_BLACK_KEY -> {
                val binding = PianoBlackKeyBinding.inflate(inflater, parent, false)
                BlackKeyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val key = keys[position]
        when (holder) {
            is WhiteKeyViewHolder -> {
                holder.bind(key)
                val touchSelector = ContextCompat.getDrawable(holder.binding.key.context, R.drawable.white_down)
                val touchSelected = ContextCompat.getDrawable(holder.binding.key.context, R.drawable.white_up)

                holder.binding.key.setPressedStateListener({
                    onPianoPlayed(key)
                }, {
                    stopSound(key)
                }, {
                    touchSelector
                }, {
                    touchSelected
                })

//                holder.binding.key.setOnTouchListener { view, event ->
//                    val action = event.action
//                    when (action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            view.background = touchSelector
//                            onPianoPlayed(key)
//                        }
//
//                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                            view.background = touchSelected
//                            stopSound(key)
//                        }
//                    }
//                    true
//                }
            }

            is BlackKeyViewHolder -> {
                holder.bind(key)

                val touchSelector = ContextCompat.getDrawable(holder.binding.key.context, R.drawable.black_down)
                val touchSelected = ContextCompat.getDrawable(holder.binding.key.context, R.drawable.black_up)

                holder.binding.key.setPressedStateListener({
                    onPianoPlayed(key)
                }, {
                    stopSound(key)
                }, {
                    touchSelector
                }, {
                    touchSelected
                })

//                holder.binding.key.setOnTouchListener { view, event ->
//                    val action = event.action
//                    when (action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            view.background = touchSelector
//                            onPianoPlayed(key)
//                        }
//
//                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                            view.background = touchSelected
//                            stopSound(key)
//                        }
//                    }
//                    true
//                }
            }
        }


    }

    override fun getItemCount(): Int {
        return keys.size
    }

    override fun getItemViewType(position: Int): Int {
        val key = keys[position]
        return if (key.isBlackKey) VIEW_TYPE_BLACK_KEY else VIEW_TYPE_WHITE_KEY
    }

    inner class WhiteKeyViewHolder(val binding: PianoWhiteKeyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(key: PianoKey) {
            binding.key.tag = key.note
        }
    }

    inner class BlackKeyViewHolder(val binding: PianoBlackKeyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(key: PianoKey) {
            binding.key.tag = key.note
        }
    }

    companion object {
        private const val VIEW_TYPE_WHITE_KEY = 0
        private const val VIEW_TYPE_BLACK_KEY = 1
    }
}

fun Button.setPressedStateListener(
    onButtonPressed: () -> Unit,
    onButtonReleased: () -> Unit,
    getPressedBackgroundDrawable: () -> Drawable?,
    getDefaultBackgroundDrawable: () -> Drawable?
) {
    var isPressed = false
    val updateButtonState = {
        if (isPressed) {
            background = getPressedBackgroundDrawable()
            onButtonPressed()

        } else {
            background = getDefaultBackgroundDrawable()
            onButtonReleased()
        }
    }

    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Button pressed
                isPressed = true
                updateButtonState()
                true
            }

            MotionEvent.ACTION_UP -> {
                // Button released
                isPressed = false
                updateButtonState()
                true
            }

            else -> false
        }
    }

    setOnClickListener {
        if (!isPressed) {
            // Handle button click event

        }
    }


}

