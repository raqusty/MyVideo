package com.video.raqust.myvideo.thirdSection.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.AttributeSet
import android.view.TextureView
import java.io.IOException

/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class VideoTextureView : TextureView, TextureView.SurfaceTextureListener {
    private var mCamera: Camera? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mCamera?.release()
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
    }


    fun startVideo() {
        // 打开摄像头并将展示方向旋转90度
        mCamera = Camera.open()
        mCamera?.setDisplayOrientation(90)
        try {
            mCamera?.setPreviewTexture(this.surfaceTexture)
            mCamera?.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopVIdeo() {
        mCamera?.release()
    }
}