package com.musique.mpiano.models

data class PianoKey(
    val note: String,
    val color: Int,
    val isBlackKey: Boolean,
    val octave: Int,
    val frequency: Float
)