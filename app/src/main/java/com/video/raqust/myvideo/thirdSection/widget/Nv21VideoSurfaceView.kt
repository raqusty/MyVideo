package com.video.raqust.myvideo.thirdSection.widget

import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class Nv21VideoSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private val mHolder by lazy { holder }
    private var mCamera: Camera? = null

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
        doChange(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mHolder.removeCallback(this)
        mCamera?.setPreviewCallback(null)
        mCamera?.stopPreview()
        mCamera?.lock()
        mCamera?.release()
        mCamera = null
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mCamera = Camera.open()
    }

    //当我们的程序开始运行，即使我们没有开始录制视频，我们的surFaceView中也要显示当前摄像头显示的内容
    private fun doChange(holder: SurfaceHolder?) {
        try {
            mCamera?.setPreviewDisplay(holder)
            //设置摄像机的预览界面  一般都是与SurfaceView#SurfaceHolder进行绑定
            //设置surfaceView旋转的角度，系统默认的录制是横向的画面

            if (mCamera != null) {
                try {
                    val parameters = mCamera?.parameters //获取摄像头参数
                    mCamera?.parameters = parameters
                } catch (e: Exception) {
                    e.printStackTrace()

                }

                mCamera?.setPreviewCallback { data, camera ->
                    //处理data
                    val mPreviewSize = camera.parameters.previewSize//获取尺寸,格式转换的时候要用到
                    //取发YUVIMAGE
                    val yuvimage = YuvImage(
                            data,
                            ImageFormat.NV21,
                            mPreviewSize.width,
                            mPreviewSize.height,
                            null)
                    val mBaos = ByteArrayOutputStream()
                    //yuvimage 转换成jpg格式
                    yuvimage.compressToJpeg(Rect(0, 0, mPreviewSize.width, mPreviewSize.height), 100, mBaos)// 80--JPG图片的质量[0-100],100最高
                    val mImageBytes = mBaos.toByteArray()

                    //将mImageBytes转换成bitmap
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.RGB_565

                    val bitmap = BitmapFactory.decodeByteArray(mImageBytes, 0, mImageBytes.size, options)
                    mCallback?.invoke(bitmap)
                }
                mCamera?.startPreview()//开始预览
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private var mCallback: ((Bitmap) -> Unit)? = null

    fun setCallback(call: ((Bitmap) -> Unit)) {
        mCallback = call
    }
}