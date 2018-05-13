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
        mBaseY = (measuredHeight shr 1).toFloat()
        mStartX0 = measuredWidth.toFloat()
        mEndX0 = (measuredWidth shr 1).toFloat()
        mCtrX0 = ((measuredWidth shr 2) * 3).toFloat()
        mEndX1 = 0f
        mCtrX1 = (measuredWidth shr 2).toFloat()
        mEndX2 = -(measuredWidth shr 1).toFloat()
        mCtrX2 = -(measuredWidth shr 2).toFloat()
    }

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
        mPath.moveTo(mEndX0, mBaseY)
        mPath.quadTo(mCtrX1, mCtrY1.toFloat(), mEndX1, mBaseY)
        if (!mStroke) {
            mPath.lineTo(mEndX1, height.toFloat())
            mPath.lineTo(mEndX0, height.toFloat())
            mPath.close()
        }
        //绘制第三个贝塞尔曲线
        mPath.moveTo(mEndX1, mBaseY)
        mPath.quadTo(mCtrX2, mCtrY0.toFloat(), mEndX2, mBaseY)
        if (!mStroke) {
            mPath.lineTo(mEndX2, height.toFloat())
            mPath.lineTo(mEndX1, height.toFloat())
            mPath.close()
        }
        canvas?.drawPath(mPath, mPaint)
        mStartX0 += mSpeed
        mEndX0 += mSpeed
        mCtrX0 += mSpeed
        mEndX1 += mSpeed
        mCtrX1 += mSpeed
        mEndX2 += mSpeed
        mCtrX2 += mSpeed

        if (mEndX2 >= 0f) {
            initCoordnate()
            mCtrY0 = mCtrY0 xor mCtrY1
            mCtrY1 = mCtrY0 xor mCtrY1
            mCtrY0 = mCtrY0 xor mCtrY1
        }
        postInvalidateDelayed(18)
    }
}