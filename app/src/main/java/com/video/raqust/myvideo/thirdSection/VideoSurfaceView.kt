package com.video.raqust.myvideo.thirdSection

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class VideoSurfaceView:SurfaceView , SurfaceHolder.Callback{

    private val mHolder by lazy { holder }
    private var mCamera:Camera?=null

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

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mCamera?.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    fun startVideo(){
        // 打开摄像头并将展示方向旋转90度
        mCamera = Camera.open()
        mCamera?.setDisplayOrientation(90)
        try {
            mCamera?.setPreviewDisplay(mHolder)
            mCamera?.startPreview()//开始预览

        } catch ( e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopVIdeo(){
        mCamera?.release()
    }
}