package com.video.raqust.myvideo.fourthSection

import android.content.Context
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import com.video.raqust.myvideo.R
import com.video.raqust.myvideo.utils.PathUtil
import kotlinx.android.synthetic.main.activity_fourth_section_media_recorder.*
import java.util.*
import android.media.AudioManager
import android.media.MediaPlayer




/**
 * Created by linzehao
 * time: 2018/10/16.
 * info:
 */
class MediaRecorderActivity : AppCompatActivity(), SurfaceHolder.Callback {

    var mContext: Context? = null

    private var mStartedFlg = false//是否正在录像

    private var mRecorder: MediaRecorder? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth_section_media_recorder)
        mContext = this

        text1.setOnClickListener {
            if (!mStartedFlg) {
                if (mRecorder == null) {
                    mRecorder = MediaRecorder()
                }

                mCamera?.unlock()
                mRecorder?.setCamera(mCamera)

                try {
                    // 这两项需要放在setOutputFormat之前
                    mRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                    mRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)

                    // Set output file format
                    mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                    // 这两项需要放在setOutputFormat之后
                    mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    mRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)

                    mRecorder?.setVideoSize(640, 480)
                    mRecorder?.setVideoFrameRate(30)
                    mRecorder?.setVideoEncodingBitRate(3 * 1024 * 1024)
                    mRecorder?.setOrientationHint(90)
                    //设置记录会话的最大持续时间（毫秒）
                    mRecorder?.setMaxDuration(30 * 1000)
                    mRecorder?.setPreviewDisplay(mSurfaceHolder?.surface)

                    val path = PathUtil.getMP4FolderPath() + "/" + getDate() + ".mp4"
                    mRecorder?.setOutputFile(path)
                    mRecorder?.prepare()
                    mRecorder?.start()

                    mStartedFlg = true

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

            }
        }

        text2.setOnClickListener {
            //stop
            if (mStartedFlg) {
                try {
                    mRecorder?.stop()
                    mRecorder?.reset()
                    mRecorder?.release()
                    if (mCamera != null) {
                        mCamera?.release()
                        mCamera = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mStartedFlg = false
        }

        val holder = media_recorder.holder
        holder.addCallback(this)
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mSurfaceHolder = holder
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
//        mSurfaceview = null
        mSurfaceHolder = null
        if (mRecorder != null) {
            mRecorder?.release()
            mRecorder = null
        }
        if (mCamera != null) {
            mCamera?.release()
            mCamera = null
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurfaceHolder = holder
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        if (mCamera != null) {


            mCamera?.setPreviewDisplay(mSurfaceHolder);
            mCamera?.setDisplayOrientation(90);
            mCamera?.startPreview()
        }



    }

    /**
     * 获取系统时间
     *
     * @return
     */
    private fun getDate(): String {
        val ca = Calendar.getInstance()
        val year = ca.get(Calendar.YEAR)           // 获取年份
        val month = ca.get(Calendar.MONTH)         // 获取月份
        val day = ca.get(Calendar.DATE)            // 获取日
        val minute = ca.get(Calendar.MINUTE)       // 分
        val hour = ca.get(Calendar.HOUR)           // 小时
        val second = ca.get(Calendar.SECOND)       // 秒
        return "" + year + (month + 1) + day + hour + minute + second
    }

    private fun playVideo(videoPath: String) {

//        mIsPlay = true
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer()
//        }
//        mediaPlayer.reset()
//        val uri = Uri.parse(videoPath)
//        mediaPlayer = MediaPlayer.create(this@ShootingActivity, uri)
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        mediaPlayer.setDisplay(mSurfaceHolder)
//        try {
//            mediaPlayer.prepare()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        mediaPlayer.start()
//        mediaPlayer.setLooping(true)//设置重复播放
    }

}