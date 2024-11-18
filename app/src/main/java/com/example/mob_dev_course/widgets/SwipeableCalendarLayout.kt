package com.example.mob_dev_course.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

class SwipeableCalendarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var x1 = 0f
    private var x2 = 0f
    private val SWIPE_THRESHOLD = 150
    private var swipeListener: SwipeListener? = null

    interface SwipeListener {
        fun onSwipeLeft()
        fun onSwipeRight()
    }

    fun setSwipeListener(listener: SwipeListener) {
        this.swipeListener = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = ev.x
            }
            MotionEvent.ACTION_UP -> {
                x2 = ev.x
                val deltaX = x2 - x1

                if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                    if (deltaX > 0) {
                        swipeListener?.onSwipeRight()
                    } else {
                        swipeListener?.onSwipeLeft()
                    }
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
