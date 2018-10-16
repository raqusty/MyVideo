package com.video.raqust.myvideo.firstSection

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView


/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class MySurfaceView : SurfaceView, SurfaceHolder.Callback, Runnable {
    private val mHolder by lazy { holder }
    private var mIsDrawing = false
    private var mCanvas: Canvas? = null
    private val TIME_IN_FRAME = 3000 // 每30帧刷新一次屏幕
    private var mBitmap: Bitmap? = null

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
        mHolder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        setZOrderOnTop(true)
        mHolder.setFormat(PixelFormat.TRANSLUCENT)
        this.keepScreenOn = true
        //mHolder.setFormat(PixelFormat.OPAQUE);
    }

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mIsDrawing = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mIsDrawing = true
        Thread(this).start()
    }

    override fun run() {
        while (mIsDrawing) {
            /**取得更新之前的时间**/
            val startTime = System.currentTimeMillis()

            /**在这里加上线程安全锁**/
            synchronized(mHolder) {
                try {
                    mCanvas = mHolder.lockCanvas()
                    draw()
                } catch (e: Exception) {
                    Log.i("linzehao", e.toString())
                } finally {
                    if (mCanvas != null)
                        mHolder.unlockCanvasAndPost(mCanvas)//保证每次都将绘图的内容提交
                }
            }

            /**取得更新结束的时间**/
            val endTime = System.currentTimeMillis()

            /**计算出一次更新的毫秒数**/
            var diffTime = (endTime - startTime)

            /**确保每次更新时间为30帧**/
            while (diffTime <= TIME_IN_FRAME) {
                diffTime = (System.currentTimeMillis() - startTime)
                /**线程等待**/
                Thread.yield()
            }
        }
    }

    private fun draw() {
        if (mBitmap != null) {
            val mPaint = Paint()
            mPaint.color = Color.WHITE
            mCanvas?.drawBitmap(mBitmap, 0f, 0f, mPaint)
        }
    }

}