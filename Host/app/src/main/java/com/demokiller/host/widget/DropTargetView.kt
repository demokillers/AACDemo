package com.demokiller.host.widget

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView


class DropTargetView : AppCompatImageView, OnDragListener {

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
                return if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v?.invalidate()
                    pvhx = PropertyValuesHolder.ofFloat("scaleX", 2f)
                    pvhy = PropertyValuesHolder.ofFloat("scaleY", 2f)
                    ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
                    true
                } else {
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 1f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 1f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                setBackgroundColor(Color.BLUE)
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                setBackgroundColor(Color.RED)
            }
            DragEvent.ACTION_DROP -> {
                setBackgroundColor(Color.RED)
                pvhx = PropertyValuesHolder.ofFloat("scaleX", 1f)
                pvhy = PropertyValuesHolder.ofFloat("scaleY", 1f)
                ObjectAnimator.ofPropertyValuesHolder(this, pvhx, pvhy).start()

                val item: ClipData.Item = event.clipData.getItemAt(0)
                val dragData = item.text
                Toast.makeText(context, "Dragged data is $dragData", Toast.LENGTH_SHORT).show()
            }
            else -> return false
        }
        return true
    }
}