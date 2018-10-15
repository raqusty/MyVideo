package com.video.raqust.myvideo.thirdSection

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import com.video.raqust.myvideo.R
import kotlinx.android.synthetic.main.activity_nv21_video.*

/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class Nv21VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nv21_video)

        nv21_video_surface_view.setCallback {
            image.setImageBitmap(rotateBitmap(it, getDegree()))
        }

    }

    private fun getDegree(): Float {
        //获取当前屏幕旋转的角度
        val rotating = this.windowManager.defaultDisplay.rotation
        var degree = 0//度数
        //根据手机旋转的角度，来设置surfaceView的显示的角度
        when (rotating) {
            Surface.ROTATION_0 -> {
                degree = 90
            }
            Surface.ROTATION_90 -> {
                degree = 0
            }
            Surface.ROTATION_180 -> {
                degree = 270
            }
            Surface.ROTATION_270 -> {
                degree = 180
            }
        }
        return degree.toFloat()
    }

    private fun rotateBitmap(origin: Bitmap?, degree: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(degree)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM.equals(origin)) {
            return newBM
        }
        origin.recycle()
        return newBM
    }


}