package com.video.raqust.myvideo.fifthSection.shapes

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by linzehao
 * time: 2018/10/18.
 * info:
 */
class Triangle {

    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"


    private var vertexBuffer: FloatBuffer? = null

    val COORDS_PER_VERTEX = 3

    private val mProgram by lazy {
        GLES20.glCreateProgram()
    }

    // 获取指向vertex shader的成员vPosition的handle
    private val mPositionHandle by lazy {
        GLES20.glGetAttribLocation(mProgram, "vPosition")
    }

    // 获取指向fragment shader的成员vColor的handle
    private val mColorHandle by lazy {
        GLES20.glGetUniformLocation(mProgram, "vColor")
    }

    private val triangleCoords by lazy {
        floatArrayOf(0.0f, 0.5f, 0.0f,// top
                -0.5f, -0.5f, 0.0f,// left
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f)//right
    }

    private val color by lazy {
        floatArrayOf(255f, 0f, 0f, 1.0f)
    }

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX

    init {
        // 初始化顶点字节缓冲区，用于存放形状的坐标
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        //设置使用设备硬件的原生字节序
        bb.order(ByteOrder.nativeOrder())
        //从ByteBuffer中创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer()
        // 把坐标都添加到FloatBuffer中
        vertexBuffer?.put(triangleCoords)
        //设置buffer从第一个坐标开始读
        vertexBuffer?.position(0)

        // 编译shader代码
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 将vertex shader添加到program
        GLES20.glAttachShader(mProgram, vertexShader)

        // 将fragment shader添加到program
        GLES20.glAttachShader(mProgram, fragmentShader)

        // 创建可执行的 OpenGL ES program
        GLES20.glLinkProgram(mProgram)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {

        //创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
        //或一个fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)

        // 将源码添加到shader并编译它
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }

    fun draw() {
        // 添加program到OpenGL ES环境中
        GLES20.glUseProgram(mProgram)


        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)

        // 获取指向fragment shader的成员vColor的handle

        //  绘制三角形
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // 禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

}
