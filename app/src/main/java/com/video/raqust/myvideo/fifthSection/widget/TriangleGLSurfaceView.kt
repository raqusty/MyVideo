package com.video.raqust.myvideo.fifthSection.widget

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.video.raqust.myvideo.fifthSection.shapes.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by linzehao
 * time: 2018/10/18.
 * info:
 */
class TriangleGLSurfaceView : GLSurfaceView, GLSurfaceView.Renderer {

    private val mTriangle by lazy { Triangle() }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        setRenderer(this)
    }

    override fun onDrawFrame(gl: GL10?) {
        mTriangle.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }


}