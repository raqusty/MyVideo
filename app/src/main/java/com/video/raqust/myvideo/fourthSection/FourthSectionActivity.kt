package com.video.raqust.myvideo.fourthSection

import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.video.raqust.myvideo.R
import com.video.raqust.myvideo.utils.PathUtil
import kotlinx.android.synthetic.main.activity_fourth_section_media_recorder.*
import java.nio.ByteBuffer
import kotlin.concurrent.thread


/**
 * Created by linzehao
 * time: 2018/10/16.
 * info:
 */
class FourthSectionActivity : AppCompatActivity() {
    var mContext: Context? = null
    var isFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth_section)

        mContext = this

        text1.setOnClickListener {
            val intent = Intent(mContext, MediaRecorderActivity::class.java)
            startActivity(intent)
        }

        text2.setOnClickListener {
            if (isFinish){
                Toast.makeText(this.mContext,"已经合并完成了",LENGTH_SHORT).show()
            }else{
                thread {
                    process()
                }
            }
        }

    }

    private val mVideoExtractor by lazy { MediaExtractor() }
    private val mAudioExtractor by lazy { MediaExtractor() }

    private val mMediaMuxer: MediaMuxer by lazy { MediaMuxer(PathUtil.getMP4FolderPath() + "/ouput.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4) }

    private fun process(): Boolean {
        isFinish = true
        mVideoExtractor.setDataSource(PathUtil.getMP4FolderPath() + "/video.mp4")
        var frameMaxInputSize = 0
        var mVideoTrackIndex = -1
        var frameRate = 0
        for (i in 0 until mVideoExtractor.trackCount) {
            val format = mVideoExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (!mime.startsWith("video/")) {
                continue
            }
            frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
            frameMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            mVideoExtractor.selectTrack(i)

            mVideoTrackIndex = mMediaMuxer.addTrack(format)

        }

        mAudioExtractor.setDataSource(PathUtil.getMP4FolderPath() + "/audio.mp4")
        var mAudioTrackIndex = -1
        var audioMaxInputSize = 0
        for (i in 0 until mAudioExtractor.trackCount) {
            val format = mAudioExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (!mime.startsWith("audio/")) {
                continue
            }
            mAudioExtractor.selectTrack(i)
            audioMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            mAudioTrackIndex = mMediaMuxer.addTrack(format)

        }

        mMediaMuxer.start()
        //视频 合成
        val videoInfo = MediaCodec.BufferInfo()
        videoInfo.presentationTimeUs = 0
        val mediaBuffer = ByteBuffer.allocate(frameMaxInputSize)
        var sampleSize = mVideoExtractor.readSampleData(mediaBuffer, 0)
        while (sampleSize > 0) {
            videoInfo.offset = 0
            videoInfo.size = sampleSize
            videoInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
            videoInfo.presentationTimeUs += (1000 * 1000 / frameRate).toLong()
            mMediaMuxer.writeSampleData(mVideoTrackIndex, mediaBuffer, videoInfo)
            mVideoExtractor.advance()
            sampleSize = mVideoExtractor.readSampleData(mediaBuffer, 0)
        }


        //音频 合成
        val audioBuffer = ByteBuffer.allocate(audioMaxInputSize)
        val audioInfo = MediaCodec.BufferInfo()
        audioInfo.presentationTimeUs = 0
        sampleSize = mAudioExtractor.readSampleData(audioBuffer, 0)
        while (sampleSize > 0) {
            audioInfo.offset = 0
            audioInfo.size = sampleSize
            audioInfo.flags = mAudioExtractor.sampleFlags
            audioInfo.presentationTimeUs += (1000 * 1000 / frameRate).toLong()
            mMediaMuxer.writeSampleData(mAudioTrackIndex, audioBuffer, audioInfo)
            mAudioExtractor.advance()
            sampleSize = mAudioExtractor.readSampleData(audioBuffer, 0)
        }

        mVideoExtractor.release()
        mAudioExtractor.release()
        mMediaMuxer.stop()
        mMediaMuxer.release()
        runOnUiThread {
            Toast.makeText(this.mContext,"合并完成",LENGTH_SHORT).show()
        }
        return true
    }
}