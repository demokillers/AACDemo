package com.demokiller.host.widget

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.appcompat.widget.AppCompatTextView


class DropTextView : AppCompatTextView, OnDragListener {

    constructor(context: Context?) : super(context) {
        // TODO Auto-generated constructor stub
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        // TODO Auto-generated constructor stub
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defaultStyle: Int) : super(context, attrs, defaultStyle) {
        // TODO Auto-generated constructor stub
        init()
    }

    @SuppressLint("NewApi")
    private fun init() {
        setOnDragListener(this)
    }

    override fun onDrag(v: View?, event: DragEvent): Boolean {
        // TODO Auto-generated method stub
        val pvhx: PropertyValuesHolder
        val pvhy: PropertyValuesHolder
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 0.5f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 0.5f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 1f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 1f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 0.75f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 0.75f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 0.5f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 0.5f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            DragEvent.ACTION_DROP -> {
                val frame0 = Keyframe.ofFloat(0f, 0.75f)
                val frame1 = Keyframe.ofFloat(0.5f, 0f)
                val frame2 = Keyframe.ofFloat(1f, 0.75f)
                pvhx = PropertyValuesHolder.ofKeyframe("scaleX", frame0, frame1, frame2)
                pvhy = PropertyValuesHolder.ofKeyframe("scaleY", frame0, frame1, frame2)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            else -> return false
        }
        return true
    }
}