package com.musique.mpiano

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect

class PianoView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var startingOctave: Int = 4
    private var totalKeys: Int = 10
    private val whiteButtonPaint = Paint()
    private val blackButtonPaint = Paint()
    private val whiteButtonDrawable = attrs?.getAttributeResourceValue(
        "http://schemas.android.com/apk/res-auto", "whiteButtonDrawable", 0
    )
    private val blackButtonDrawable = attrs?.getAttributeResourceValue(
        "http://schemas.android.com/apk/res-auto", "blackButtonDrawable", 0
    )

    private var whiteButtons = mutableListOf<PianoButton>()
    private var blackButtons = mutableListOf<PianoButton>()

    var onPianoClickListener: OnPianoClickListener? = null

    init {
        whiteButtonPaint.color = Color.WHITE
        blackButtonPaint.color = Color.BLACK
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PianoView)
            startingOctave = typedArray.getInteger(R.styleable.PianoView_startingOctave, 4)
            totalKeys = typedArray.getInteger(R.styleable.PianoView_totalKeys, 10)
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val whiteButtonWidth = width / totalKeys
        val whiteButtonHeight = height
        val blackButtonWidth = whiteButtonWidth * 2 / 3
        val blackButtonHeight = height * 2 / 3

        whiteButtons.clear()
        blackButtons.clear()
        var bi = 0
        for (i in 0 until totalKeys) {
            val left = i * whiteButtonWidth
            val right = left + whiteButtonWidth
            val rect = RectF(left.toFloat(), 0f, right.toFloat(), whiteButtonHeight.toFloat())

            if (i % 7 != 2 && i % 7 != 6) {
                // Add black keys

                val blackLeft = right - blackButtonWidth / 2
                val blackRight = blackLeft + blackButtonWidth
                val blackRect = RectF(blackLeft.toFloat(), 0f, blackRight.toFloat(), blackButtonHeight.toFloat())
                val button =
                    PianoButton(context, blackButtonPaint, blackButtonDrawable ?: 0, blackRect, next7BlackKeys.elementAt(bi))
                blackButtons.add(button)
                bi++
            }

            val button = PianoButton(context, whiteButtonPaint, whiteButtonDrawable ?: 0, rect, next10WhiteKeys.elementAt(i))
            whiteButtons.add(button)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw white buttons
        whiteButtons.forEach { button ->
            button.draw(canvas)
        }

        // Draw black buttons
        blackButtons.forEach { button ->
            button.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchDown(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                handleTouchMove(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_UP -> {
                // Release all buttons
                whiteButtons.forEach {
                    it.isPressed = false
                }
                blackButtons.forEach {
                    it.isPressed = false
                }
                onPianoClickListener?.onKeyReleased()
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun handleTouchDown(x: Float, y: Float) {
        startX = x
        startY = y
        val blackButtonPressed = blackButtons.find { it.contains(x, y) }
        val whiteButtonPressed = whiteButtons.find { it.contains(x, y) }
        // Check if any black button is pressed
        if (blackButtonPressed != null) {
            blackButtonPressed.isPressed = true
            onPianoClickListener?.onBlackKeyPressed(blackButtonPressed.note)
            invalidate()

        }
        // Check if any white button is pressed
        else if (whiteButtonPressed != null) {
            whiteButtonPressed.isPressed = true
            onPianoClickListener?.onWhiteKeyPressed(whiteButtonPressed.note)
            invalidate()
        }
    }

    private var startX: Float = 0f
    private var startY: Float = 0f

    private fun handleTouchMove(x: Float, y: Float) {
        // Calculate the horizontal distance moved
        val deltaX = x - startX

        // Check if a left-to-right swipe occurred
        if (deltaX > 0) {
            // Find all black buttons intersected by the swipe
            val blackButtonsIntersected = blackButtons.filter { it.intersects(startX, startY, x, y) }

            // Find all white buttons intersected by the swipe
            val whiteButtonsIntersected = whiteButtons.filter { it.intersects(startX, startY, x, y) }

            // Invoke the appropriate callbacks for black and white keys
            blackButtonsIntersected.forEach { button ->
                onPianoClickListener?.onBlackKeyPressed(button.note)
            }

            whiteButtonsIntersected.forEach { button ->
                onPianoClickListener?.onWhiteKeyPressed(button.note)
            }
        }
        // Check if a right-to-left swipe occurred
        else if (deltaX < 0) {
            // Find all black buttons intersected by the swipe
            val blackButtonsIntersected = blackButtons.filter { it.intersects(x, y, startX, startY) }

            // Find all white buttons intersected by the swipe
            val whiteButtonsIntersected = whiteButtons.filter { it.intersects(x, y, startX, startY) }

            // Invoke the appropriate callbacks for black and white keys
            blackButtonsIntersected.forEach { button ->
                onPianoClickListener?.onBlackKeyPressed(button.note)
            }

            whiteButtonsIntersected.forEach { button ->
                onPianoClickListener?.onWhiteKeyPressed(button.note)
            }
        }
    }


    interface OnPianoClickListener {
        fun onWhiteKeyPressed(note: String)
        fun onBlackKeyPressed(note: String)
        fun onKeyReleased()
    }

    private inner class PianoButton(
        context: Context,
        private val buttonPaint: Paint,
        private val buttonDrawableResId: Int,
        private val rect: RectF,
        var note: String
    ) {

        var isPressed = false

        init {
            buttonPaint.isAntiAlias = true
        }

        fun draw(canvas: Canvas) {
            // Draw the button background
            canvas.drawRect(rect, buttonPaint)

            // Draw the button drawable if available
            if (buttonDrawableResId != 0) {
                val drawable = context.getDrawable(buttonDrawableResId)
                drawable?.bounds = rect.toRect()
                drawable?.draw(canvas)
            }

            // Draw a border around the button if pressed
            if (isPressed) {
                val borderPaint = Paint().apply {
                    color = Color.BLUE
                    style = Paint.Style.STROKE
                    strokeWidth = 8f
                }
                canvas.drawRect(rect, borderPaint)
            }
        }

        fun contains(x: Float, y: Float): Boolean {
            // For black buttons, check if the touch is within the bounds of the button only
            if (buttonPaint.color == Color.BLACK) {
                val inset = rect.width() * 0.1f // Adjust the inset value as needed
                val blackRect = RectF(rect.left + inset, rect.top, rect.right - inset, rect.bottom)
                return blackRect.contains(x, y)
            }

            // For white buttons, check if the touch is within the bounds of the entire button
            return rect.contains(x, y)
        }

        fun intersects(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
            // Create a RectF object for the swipe path
            val swipeRect = RectF(startX, startY, endX, endY)

            // Check if the swipe path intersects with the button's boundaries
            return swipeRect.intersect(rect)
        }


    }


    val next10WhiteKeys = whiteNotesFrequencies
        .filterKeys { it.substring(1).toInt() >= startingOctave && it.substring(1).toInt() < startingOctave + 2 }
        .keys.take(10)

    val next7BlackKeys = blackNotesFrequencies
        .filterKeys { it.substring(2).toInt() >= startingOctave && it.substring(2).toInt() < startingOctave + 2 }
        .keys.take(7)

}
