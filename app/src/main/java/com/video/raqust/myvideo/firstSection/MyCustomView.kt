package com.video.raqust.myvideo.firstSection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class MyCustomView : View {
    private var mBitmap: Bitmap? = null
    private val mPaint by lazy { Paint() }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    private fun initView() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
    }

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (mBitmap != null) {
            canvas?.drawBitmap(mBitmap, 0f, 0f, mPaint)
        }
    }
}