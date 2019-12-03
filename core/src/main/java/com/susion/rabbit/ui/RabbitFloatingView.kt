package com.susion.rabbit.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import com.susion.rabbit.Rabbit
import com.susion.rabbit.R
import com.susion.rabbit.utils.RabbitUiUtils
import com.susion.rabbit.utils.getColor
import kotlinx.android.synthetic.main.rabbit_view_floating.view.*
import kotlin.math.abs

/**
 * susionwang at 2019-09-23
 */
class RabbitFloatingView(context: Context) : LinearLayout(context) {

    private var mXInScreen: Float = 0.toFloat()
    private var mYInScreen: Float = 0.toFloat()
    private var isShow = false

    private val mWindowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val mParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_floating, this)
        orientation = VERTICAL
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isClickable = true

        mDevToolsFloatingIv.setOnClickListener {
            Rabbit.uiManager.handleFloatingViewClickEvent()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY
                updateViewPosition()
            }
            MotionEvent.ACTION_UP -> {
                moveToEdge()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * show this tacker float view
     */
    fun show() {
        if (isShow) return

        isShow = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        }

        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mParams.format = PixelFormat.RGBA_8888
        mParams.gravity = Gravity.START or Gravity.TOP
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.x = RabbitUiUtils.getScreenWidth()
        mParams.y = (RabbitUiUtils.getScreenHeight() / 2) * 1
        mParams.windowAnimations = android.R.style.Animation_Toast
        mWindowManager.addView(this, mParams)
    }

    fun hide() {
        isShow = false
        mWindowManager.removeView(this)
    }

    /**
     * update this tracker float position
     */
    private fun updateViewPosition() {
        mParams.x = (mXInScreen - width).toInt()
        mParams.y = (mYInScreen - height).toInt()
        mWindowManager.updateViewLayout(this, mParams)
    }

    /**
     * move this to edge
     */
    private fun moveToEdge() {
        val start = mXInScreen
        val screenWidth = mWindowManager.defaultDisplay?.width
        val end: Float
        end = if (mXInScreen > (screenWidth ?: 0) / 2) {
            (screenWidth ?: 0).toFloat()
        } else {
            0f
        }
        val time = abs(start - end).toLong() * 800 / (screenWidth ?: 0)
        val animator = ValueAnimator.ofFloat(start, end).setDuration(time)
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mParams.x = value.toInt()
            mWindowManager.updateViewLayout(this@RabbitFloatingView, mParams)
        }
        animator.start()
    }

    fun updateFps(fpsValue: Float) {
        if (fpsValue == 0f) {
            mDevToolsFloatingTvFps.visibility = View.GONE
            return
        }

        mDevToolsFloatingTvFps.visibility = View.VISIBLE
        mDevToolsFloatingTvFps.text = "${fpsValue.toInt()}"
        val textColor = if (fpsValue < 45) R.color.rabbit_error_red else R.color.rabbit_black
        mDevToolsFloatingTvFps.setTextColor(getColor(context, textColor))
    }

    fun updateMemorySize(memorySize: Int) {
        if (memorySize == 0) {
            mDevToolsFloatingTvMemory.visibility = View.GONE
            return
        }

        mDevToolsFloatingTvMemory.visibility = View.VISIBLE
        mDevToolsFloatingTvMemory.text = "${RabbitUiUtils.formatFileSize(memorySize.toLong())}"
    }

}