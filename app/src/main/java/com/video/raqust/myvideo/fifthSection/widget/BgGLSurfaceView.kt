package com.video.raqust.myvideo.fifthSection.widget

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by linzehao
 * time: 2018/10/18.
 * info:
 */
class BgGLSurfaceView : GLSurfaceView, GLSurfaceView.Renderer {

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

        //GLSurfaceView.RENDERMODE_CONTINUOUSLY 不间断的绘制,默认渲染模式是这种
        //renderMode=GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // 在屏幕变脏时绘制,也就是当调用GLSurfaceView的requestRender ()方法后才会执行一次(第一次运行的时候会自动绘制一次)
        renderMode=GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onDrawFrame(gl: GL10?) {
        gl?.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Log.i("linzehao","onDrawFrame")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.glClearColor(110.0f, 0.0f, 110.0f, 1.0f)
    }

}