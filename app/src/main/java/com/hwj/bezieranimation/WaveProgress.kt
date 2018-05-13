package com.hwj.bezieranimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveProgress(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {


    private val mPaint = Paint()
    private val mPath = Path()
    private var mSpeed = 5

    private var mBaseY = 0f
    private var mCtrX0 = 0f//第一个贝塞尔曲线的控制点X坐标
    private var mCtrY0 = 0//第一个贝塞尔曲线的控制点Y坐标,这里要用Int类型，因为kotlin里float不能进行xor运算
    private var mCtrX1 = 0f//第二个贝塞尔曲线的控制点X坐标
    private var mCtrY1 = 0//第二个贝塞尔曲线的控制点Y坐标，这里要用Int类型，因为kotlin里float不能进行xor运算
    private var mCtrX2 = 0f//第三个贝塞尔曲线的控制点X坐标

    //第一个贝塞尔曲线起始点和终止点
    private var mStartX0 = 0f
    private var mEndX0 = 0f
    //第二个贝塞尔曲线终止点
    private var mEndX1 = 0f
    //第三个贝塞尔曲线终止点
    private var mEndX2 = 0f
    //波形的幅度
    private var mAmplitude = 30


    private var mProgress = 0
    private var mCurrentProgress = 0
    private var mWaveColor = Color.BLUE
    private var mBackgroundColor = Color.TRANSPARENT
    private val mRoundPath = Path()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        attrs?.run {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgress)
            if (typeArray.hasValue(R.styleable.WaveProgress_wave_progress_speed)) {
                mSpeed = typeArray.getDimensionPixelOffset(R.styleable.WaveProgress_wave_progress_speed, 5)
            }
            if (typeArray.hasValue(R.styleable.WaveProgress_wave_progress_line_width)) {
                mPaint.strokeWidth = typeArray.getDimensionPixelOffset(R.styleable.WaveProgress_wave_progress_line_width, 5).toFloat()
            }
            if (typeArray.hasValue(R.styleable.WaveProgress_wave_progress_amplitude)) {
                mAmplitude = typeArray.getDimensionPixelOffset(R.styleable.WaveProgress_wave_progress_amplitude, 30)
            }
            if (typeArray.hasValue(R.styleable.WaveProgress_wave_progress_wave_color)) {
                mWaveColor = typeArray.getColor(R.styleable.WaveProgress_wave_progress_wave_color, Color.BLUE)
            }
            typeArray.recycle()
        }
        val systemAttrs = intArrayOf(android.R.attr.background)
        val systemTypedArray = context.obtainStyledAttributes(attrs, systemAttrs)
        mBackgroundColor = if (systemTypedArray.hasValue(0)) {
            systemTypedArray.getColor(0, Color.TRANSPARENT)
        } else {
            Color.TRANSPARENT
        }
        systemTypedArray.recycle()
        mPaint.style = Paint.Style.FILL
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initCoordnate()
        mCtrY1 = measuredHeight + (mAmplitude shr 1)
        mCtrY0 = measuredHeight - (mAmplitude shr 1)
        mBaseY = measuredHeight.toFloat()
        mRoundPath.addCircle((measuredHeight / 2).toFloat(), (measuredHeight / 2).toFloat(), (measuredHeight / 2).toFloat(), Path.Direction.CCW)
    }

    private fun initCoordnate() {
        mStartX0 = measuredWidth.toFloat()
        mEndX0 = (measuredWidth / 2).toFloat()
        mCtrX0 = (measuredWidth * 3 / 4).toFloat()
        mEndX1 = 0f
        mCtrX1 = (measuredWidth / 4).toFloat()
        mEndX2 = (-measuredWidth / 2).toFloat()
        mCtrX2 = (-measuredWidth / 4).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (mCurrentProgress >= 100) {
            mPaint.color = mWaveColor
            canvas.drawCircle((height / 2).toFloat(), (height / 2).toFloat(), (height / 2).toFloat(), mPaint)
            drawProgressText(canvas)
            return
        }
        canvas.clipPath(mRoundPath)
        mPath.reset()
        mPaint.color = mBackgroundColor
        canvas.drawCircle((height / 2).toFloat(), (height / 2).toFloat(), (height / 2).toFloat(), mPaint)
        mPaint.color = mWaveColor

        //绘制第一个贝塞尔曲线
        mPath.moveTo(mStartX0, mBaseY)
        mPath.quadTo(mCtrX0, mCtrY0.toFloat(), mEndX0, mBaseY)
        mPath.lineTo(mEndX0, height.toFloat())
        mPath.lineTo(mStartX0, height.toFloat())
        mPath.close()

        //绘制第二个贝塞尔曲线
        mPath.moveTo(mEndX0, mBaseY)
        mPath.quadTo(mCtrX1, mCtrY1.toFloat(), mEndX1, mBaseY)
        mPath.lineTo(mEndX1, height.toFloat())
        mPath.lineTo(mEndX0, height.toFloat())
        mPath.close()
        //绘制第三个贝塞尔曲线
        mPath.moveTo(mEndX1, mBaseY)
        mPath.quadTo(mCtrX2, mCtrY0.toFloat(), mEndX2, mBaseY)
        mPath.lineTo(mEndX2, height.toFloat())
        mPath.lineTo(mEndX1, height.toFloat())
        mPath.close()
        canvas.drawPath(mPath, mPaint)
        drawProgressText(canvas)

        mStartX0 += mSpeed
        mEndX0 += mSpeed
        mCtrX0 += mSpeed
        mEndX1 += mSpeed
        mCtrX1 += mSpeed
        mEndX2 += mSpeed
        mCtrX2 += mSpeed

        if (mCurrentProgress != mProgress) {
            val step = height / 100 * (mProgress - mCurrentProgress)
            mCtrY1 -= step
            mCtrY0 -= step
            mBaseY -= step
            mCurrentProgress = mProgress
        }
        if (mEndX2 >= 0f) {
            initCoordnate()
            mCtrY0 = mCtrY0 xor mCtrY1
            mCtrY1 = mCtrY0 xor mCtrY1
            mCtrY0 = mCtrY0 xor mCtrY1
        }
        postInvalidateDelayed(18)
    }

    private fun drawProgressText(canvas: Canvas) {
        mPaint.color = Color.BLACK
        mPaint.textSize = mAmplitude.toFloat()
        mPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("$mCurrentProgress%", (measuredHeight / 2).toFloat(), (measuredHeight / 2).toFloat(), mPaint)

    }

    fun setProgress(progress: Int) {
        mProgress = progress
    }
}