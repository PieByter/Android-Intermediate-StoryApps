package com.example.storyapps.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapps.R

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {

    private var errorMessage: String? = null
    private var passwordIcon: Drawable? = null

    init {
        setupIcon()
        setupTextWatcher()
    }

    private fun setupIcon() {
        passwordIcon = ContextCompat.getDrawable(context, R.drawable.ic_password)
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, passwordIcon, null)
    }

    private fun setupTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < 8) {
                    errorMessage = context.getString(R.string.password_warning)
                    error = errorMessage
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val drawableEnd = compoundDrawablesRelative[2]
        if (drawableEnd != null && event.action == MotionEvent.ACTION_UP) {
            if (event.x >= (width - paddingEnd - drawableEnd.bounds.width())) {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        togglePasswordVisibility()
        return true
    }

    private fun togglePasswordVisibility() {
        transformationMethod = if (transformationMethod == null) {
            PasswordTransformationMethod.getInstance()
        } else {
            null
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, passwordIcon, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }
}
