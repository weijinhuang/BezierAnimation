package com.hwj.bezieranimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * 贝塞尔曲线实现的波浪效果
 * @author H.W.J
 */
class BezierWaveView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val mPaint = Paint()
    private val mPath = Path()
    private var mSpeed = 5
    private var mStroke = false

    private var mBaseY = 0f
    private var mCtrX0 = 0f//第一个贝塞尔曲线的控制点X坐标
    private var mCtrY0 = 0//第一个贝塞尔曲线的控制点Y坐标
    private var mCtrX1 = 0f//第二个贝塞尔曲线的控制点X坐标
    private var mCtrY1 = 0//第二个贝塞尔曲线的控制点Y坐标
    private var mCtrX2 = 0f//第三个贝塞尔曲线的控制点X坐标

    //第一个贝塞尔曲线起始点和终止点
    private var mStartX0 = 0f
    private var mEndX0 = 0f
    //第二个贝塞尔曲线起始点和终止点
    private var mStartX1 = 0f
    //第三个贝塞尔曲线起始点和终止点
    private var mStartX2 = 0f

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        attrs?.run {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.BezierWaveView)
            if (typeArray.hasValue(R.styleable.BezierWaveView_speed)) {
                mSpeed = typeArray.getDimensionPixelOffset(R.styleable.BezierWaveView_speed, 5)
            }
            if (typeArray.hasValue(R.styleable.BezierWaveView_stroke)) {
                mStroke = typeArray.getBoolean(R.styleable.BezierWaveView_stroke, false)
            }
            if (typeArray.hasValue(R.styleable.BezierWaveView_line_width)) {
                mPaint.strokeWidth = typeArray.getDimensionPixelOffset(R.styleable.BezierWaveView_line_width, 5).toFloat()
            }
            typeArray.recycle()
        }
        val systemAttrs = intArrayOf(android.R.attr.background)
        val systemTypedArray = context.obtainStyledAttributes(attrs, systemAttrs)
        if (systemTypedArray.hasValue(0)) {
            val color = systemTypedArray.getColor(0, Color.YELLOW)
            mPaint.color = color
        } else {
            mPaint.color = Color.YELLOW
        }
        systemTypedArray.recycle()
        mPaint.style = if (mStroke) Paint.Style.STROKE else Paint.Style.FILL

        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initCoordnate()
        mCtrY1 = measuredHeight
        mCtrY0 = 0
    }

    private fun initCoordnate() {
        mBaseY = (measuredHeight / 2).toFloat()

        mStartX0 = (measuredWidth / 2).toFloat()
        mEndX0 = measuredWidth.toFloat()
        mCtrX0 = (measuredWidth * 3 / 4).toFloat()

        mStartX1 = 0f
        mCtrX1 = (measuredWidth / 4).toFloat()

        mStartX2 = (-measuredWidth / 2).toFloat()
        mCtrX2 = (-measuredWidth / 4).toFloat()
    }

    private var mCount = 0
    override fun onDraw(canvas: Canvas?) {
        mPath.reset()
        //绘制第一个贝塞尔曲线
        mPath.moveTo(mStartX0, mBaseY)
        mPath.quadTo(mCtrX0, mCtrY0.toFloat(), mEndX0, mBaseY)
        if (!mStroke) {
            mPath.lineTo(mEndX0, height.toFloat())
            mPath.lineTo(mStartX0, height.toFloat())
            mPath.close()
        }

        //绘制第二个贝塞尔曲线
        mPath.moveTo(mStartX1, mBaseY)
        mPath.quadTo(mCtrX1, mCtrY1.toFloat(), mStartX0, mBaseY)
        if (!mStroke) {
            mPath.lineTo(mStartX0, height.toFloat())
            mPath.lineTo(mStartX1, height.toFloat())
            mPath.close()
        }
        //绘制第三个贝塞尔曲线
        mPath.moveTo(mStartX2, mBaseY)
        mPath.quadTo(mCtrX2, mCtrY0.toFloat(), mStartX1, mBaseY)
        if (!mStroke) {
            mPath.lineTo(mStartX1, height.toFloat())
            mPath.lineTo(mStartX2, height.toFloat())
            mPath.close()
        }

        canvas?.drawPath(mPath, mPaint)
        mStartX0 += mSpeed
        mEndX0 += mSpeed
        mCtrX0 += mSpeed
        mStartX1 += mSpeed
        mCtrX1 += mSpeed
        mStartX2 += mSpeed
        mCtrX2 += mSpeed

        if (mStartX2 >= 0f) {
            initCoordnate()
            mCtrY0 = mCtrY0 xor mCtrY1
            mCtrY1 = mCtrY0 xor mCtrY1
            mCtrY0 = mCtrY0 xor mCtrY1
        }
        postInvalidateDelayed(18)
    }
}