package com.example.storyapps.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapps.R

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var errorMessage: String? = null

    private var clearButtonImage: Drawable? = null

    init {
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_close)

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
                error = if (s != null && !Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    errorMessage ?: "Invalid email format"
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawablesRelative[2] != null) {
            val clearButtonImage = compoundDrawablesRelative[2]
            val clearButtonStart: Float
            val clearButtonEnd: Float

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage!!.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            showClearButton()
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            text?.clear()
                            hideClearButton()
                            return true
                        }
                    }
                }
            } else {
                clearButtonStart =
                    (width - paddingEnd - clearButtonImage!!.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            showClearButton()
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            text?.clear()
                            hideClearButton()
                            return true
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

}
