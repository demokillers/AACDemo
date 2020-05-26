package com.demokiller.host.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class MarqueeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : TextView(context, attrs, defStyle) {
    /** 滚动器  */
    private var mScroller: Scroller? = null

    /** 获取滚动一次的时间  */
    /** 设置滚动一次的时间  */
    /** 滚动一次的时间  */
    var rndDuration = ROLLING_INTERVAL_DEFAULT

    /** 滚动的初始 X 位置  */
    private var mXPaused = 0

    /** 是否暂停  */
    var isPaused = true
        private set

    /** 是否第一次  */
    private var mFirst = true

    /** 获取滚动模式  */
    /** 设置滚动模式  */
    /** 滚动模式  */
    var scrollMode = SCROLL_ONCE

    /** 获取第一次滚动延迟  */
    /** 设置第一次滚动延迟  */
    /** 初次滚动时间间隔  */
    var scrollFirstDelay = FIRST_SCROLL_DELAY_DEFAULT

    var mDistance = 0
    var mDuration = 0
    private var mHandler = Handler(Looper.getMainLooper())
    private var mResetRunnable = Runnable { scrollX = 0 }
    private var mScrollRunnable = Runnable {
        mScroller?.startScroll(mXPaused, 0, mDistance, 0, mDuration)
        invalidate()
        isPaused = false
    }

    init {
        initView()
    }

    private fun initView() {
        setSingleLine()
        ellipsize = null
    }

    /**
     * 开始滚动
     */
    fun startScroll() {
        mXPaused = 0
        isPaused = false
        mFirst = true
        resumeScroll()
    }

    /**
     * 继续滚动
     */
    private fun resumeScroll() {
        if (isPaused) return
        // 设置水平滚动
        setHorizontallyScrolling(true)

        // 使用 LinearInterpolator 进行滚动
        if (mScroller == null) {
            mScroller = Scroller(this.context, LinearInterpolator())
            setScroller(mScroller)
        }
        val scrollingLen = calculateScrollingLen()
        mDistance = scrollingLen - mXPaused
        mDuration = (rndDuration * mDistance * 1.00000
                / scrollingLen).toInt()
        if (mFirst) {
            mHandler.postDelayed(mScrollRunnable, scrollFirstDelay.toLong())
        } else {
            mScroller?.startScroll(mXPaused, 0, mDistance, 0, mDuration)
            invalidate()
            isPaused = false
        }
    }

    /**
     * 暂停滚动
     */
    fun pauseScroll() {
        if (null == mScroller) return
        if (isPaused) return
        isPaused = true
        mXPaused = mScroller?.currX ?: 0
        mScroller?.abortAnimation()
    }

    /**
     * 停止滚动，并回到初始位置
     */
    private fun stopScroll() {
        if (null == mScroller) {
            return
        }
        isPaused = true
        mHandler.postDelayed(mResetRunnable, 1000L)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScroller?.forceFinished(true)
        mHandler.removeCallbacks(mResetRunnable)

    }

    /**
     * 计算滚动的距离
     *
     * @return 滚动的距离
     */
    private fun calculateScrollingLen(): Int {
        val strTxt = text.toString()
        return paint?.measureText(strTxt)?.toInt()?.minus(width) ?: 0
    }

    override fun computeScroll() {
        super.computeScroll()
        mScroller?.let {
            if (it.isFinished && !isPaused) {
                if (scrollMode == SCROLL_ONCE) {
                    stopScroll()
                    return
                }
                isPaused = false
                mXPaused = -width
                mFirst = false
                resumeScroll()
            }
        }
    }

    companion object {
        /** 默认滚动时间  */
        private const val ROLLING_INTERVAL_DEFAULT = 1000

        /** 第一次滚动默认延迟  */
        private const val FIRST_SCROLL_DELAY_DEFAULT = 100

        /** 滚动模式-一直滚动  */
        const val SCROLL_FOREVER = 100

        /** 滚动模式-只滚动一次  */
        const val SCROLL_ONCE = 101
    }
}