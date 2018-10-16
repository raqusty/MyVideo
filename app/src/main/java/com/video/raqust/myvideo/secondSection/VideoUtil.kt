package com.video.raqust.myvideo.secondSection

import android.app.Application
import android.media.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.video.raqust.myvideo.utils.PathUtil
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by linzehao
 * time: 2018/10/11.
 * info: 音源的采集,播放工具
 * AudioRecord 和 AudioTrack API 完成音频 PCM 数据的采集和播放，并实现读写音频 wav 文件
 */
object VideoUtil {
    private val TAG = "VideoUtil"
    private val RECORD_AUDIO_BUFFER_TIMES = 1
    private val PLAY_AUDIO_BUFFER_TIMES = 1
    private val AUDIO_FREQUENCY = 44100

    private val RECORD_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
    private val PLAY_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO
    private val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

    private val cachePCMFolder by lazy {
        PathUtil.getCachePCMFolderPath()
    }

    private val wavFolderPath by lazy {
        PathUtil.getWaveFolderPath()
    }

    private var mContext: Application? = null

    @Volatile
    private var mWindState = WindState.IDLE // 当前状态

    private var tmpPCMFile: File? = null
    private var tmpWavFile: File? = null

    private var aRecordThread: AudioRecordThread? = null           // 录制线程
    private var onStateListener: OnState? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 表示当前状态
     */
    enum class WindState {
        ERROR,
        IDLE,
        RECORDING,
        STOP_RECORD,
        PLAYING,
        STOP_PLAY
    }

    fun setOnStateListener(onStateListener: OnState) {
        this.onStateListener = onStateListener
    }

    /**
     * 初始化目录
     */
    fun init(context: Application) {
        // 存储在App内  也可以存在SD卡上
        mContext = context

        val folder = File(cachePCMFolder)
        if (!folder.exists()) {
            val f = folder.mkdirs()
            Log.d(TAG, String.format(Locale.CHINA, "PCM目录:%s -> %b", cachePCMFolder, f))
        } else {
            for (f in folder.listFiles()) {
                val d = f.delete()
                Log.d(TAG, String.format(Locale.CHINA, "删除PCM文件:%s %b", f.getName(), d))
            }
            Log.d(TAG, String.format(Locale.CHINA, "PCM目录:%s", cachePCMFolder))
        }

        val wavDir = File(wavFolderPath)
        if (!wavDir.exists()) {
            val w = wavDir.mkdirs()
            Log.d(TAG, String.format(Locale.CHINA, "wav目录:%s -> %b", wavFolderPath, w))
        } else {
            Log.d(TAG, String.format(Locale.CHINA, "wav目录:%s", wavFolderPath))
        }
    }

    /**
     * 开始录制音频
     */
    @Synchronized
    fun startRecord(createWav: Boolean) {
        if (mWindState != WindState.IDLE) {
            Log.w(TAG, "无法开始录制，当前状态为 $mWindState")
            return
        }
        try {
            tmpPCMFile = File.createTempFile("recording", ".pcm", File(cachePCMFolder))
            if (createWav) {
                val sdf = SimpleDateFormat("yyMMdd_HHmmss", Locale.CHINA)
                tmpWavFile = File(wavFolderPath + File.separator + "r" + sdf.format(Date()) + ".wav")
            }
            Log.d(TAG, "tmp file " + tmpPCMFile?.name)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (null != aRecordThread) {
            aRecordThread?.interrupt()
            aRecordThread = null
        }
        aRecordThread = AudioRecordThread(createWav)
        aRecordThread?.start()
    }

    @Synchronized
    fun stopRecord() {
        if (mWindState != WindState.RECORDING) {
            return
        }
        mWindState = WindState.STOP_RECORD
        notifyState(mWindState)
    }

    /**
     * 播放录制好的PCM文件
     */
    @Synchronized
    fun startPlayPCM() {
        if (!isIdle()) {
            return
        }
        AudioTrackPlayThread(tmpPCMFile).start()
    }

    /**
     * 播放录制好的wav文件
     */
    @Synchronized
    fun startPlayWav() {
        if (!isIdle()) {
            return
        }
        AudioTrackPlayThread(tmpWavFile).start()
    }

    @Synchronized
    fun stopPlay() {
        if (mWindState != WindState.PLAYING) {
            return
        }
        mWindState = WindState.STOP_PLAY
    }

    @Synchronized
    private fun isIdle(): Boolean {
        return WindState.IDLE == mWindState
    }

    /**
     * 音频录制线程
     * 使用FileOutputStream来写文件
     */
    private class AudioRecordThread internal constructor(createWav: Boolean) : Thread() {
        internal var aRecord: AudioRecord
        internal var bufferSize = 10240
        internal var createWav = false

        init {
            this.createWav = createWav
            bufferSize = AudioRecord.getMinBufferSize(AUDIO_FREQUENCY,
                    RECORD_CHANNEL_CONFIG, AUDIO_ENCODING) * RECORD_AUDIO_BUFFER_TIMES
            Log.d(TAG, "record buffer size = $bufferSize")
            aRecord = AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_FREQUENCY,
                    RECORD_CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize)
        }

        override fun run() {
            mWindState = WindState.RECORDING
            notifyState(mWindState)
            Log.d(TAG, "录制开始")
            try {
                // 这里选择FileOutputStream而不是DataOutputStream
                val pcmFos = FileOutputStream(tmpPCMFile)

                val wavFos = FileOutputStream(tmpWavFile)
                if (createWav) {
                    writeWavFileHeader(wavFos, bufferSize.toLong(), AUDIO_FREQUENCY.toLong(), aRecord.channelCount)
                }
                aRecord.startRecording()
                val byteBuffer = ByteArray(bufferSize)
                while (mWindState.equals(WindState.RECORDING) && !isInterrupted) {
                    val end = aRecord.read(byteBuffer, 0, byteBuffer.size)
                    pcmFos.write(byteBuffer, 0, end)
                    pcmFos.flush()
                    if (createWav) {
                        wavFos.write(byteBuffer, 0, end)
                        wavFos.flush()
                    }
                }
                aRecord.stop() // 录制结束
                pcmFos.close()
                wavFos.close()
                if (createWav) {
                    // 修改header
                    val pcmFis = FileInputStream(tmpWavFile)
                    val wavRaf = RandomAccessFile(tmpWavFile, "rw")
                    val header = generateWavFileHeader(pcmFis.channel.size(), AUDIO_FREQUENCY.toLong(), aRecord.channelCount)
                    wavRaf.seek(0)
                    wavRaf.write(header)
                    wavRaf.close()
                    pcmFis.close()
                }
                Log.i(TAG, "audio tmp file len: " + tmpPCMFile?.length())
            } catch (e: Exception) {
                Log.e(TAG, "AudioRecordThread:", e)
                notifyState(WindState.ERROR)
            }

            notifyState(mWindState)
            mWindState = WindState.IDLE
            notifyState(mWindState)
            Log.d(TAG, "录制结束")
        }

    }

    /**
     * AudioTrack播放音频线程
     * 使用FileInputStream读取文件
     */
    private class AudioTrackPlayThread internal constructor(aFile: File?) : Thread() {
        internal var track: AudioTrack
        internal var bufferSize = 10240
        internal var audioFile: File? = null

        init {
            priority = Thread.MAX_PRIORITY
            audioFile = aFile
            val bufferSize = AudioTrack.getMinBufferSize(AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING) * PLAY_AUDIO_BUFFER_TIMES
            track = AudioTrack(AudioManager.STREAM_MUSIC,
                    AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize,
                    AudioTrack.MODE_STREAM)
        }

        override fun run() {
            super.run()
            mWindState = WindState.PLAYING
            notifyState(mWindState)
            try {
                val fis = FileInputStream(audioFile)
                track.play()
                val aByteBuffer = ByteArray(bufferSize)
                while (mWindState == WindState.PLAYING && fis.read(aByteBuffer) >= 0) {
                    track.write(aByteBuffer, 0, aByteBuffer.size)
                }
                track.stop()
                track.release()
            } catch (e: Exception) {
                Log.e(TAG, "AudioTrackPlayThread:", e)
                notifyState(WindState.ERROR)
            }

            mWindState = WindState.STOP_PLAY
            notifyState(mWindState)
            mWindState = WindState.IDLE
            notifyState(mWindState)
        }

    }

    @Synchronized
    private fun notifyState(currentState: WindState) {
        if (null != onStateListener) {
            mainHandler.post { onStateListener!!.onStateChanged(currentState) }
        }
    }

    interface OnState {
        fun onStateChanged(currentState: WindState)
    }


    /**
     * @param out            wav音频文件流
     * @param totalAudioLen  不包括header的音频数据总长度
     * @param longSampleRate 采样率,也就是录制时使用的频率
     * @param channels       audioRecord的频道数量
     * @throws IOException 写文件错误
     */
    @Throws(IOException::class)
    private fun writeWavFileHeader(out: FileOutputStream, totalAudioLen: Long, longSampleRate: Long,
                                   channels: Int) {
        val header = generateWavFileHeader(totalAudioLen, longSampleRate, channels)
        out.write(header, 0, header.size)
    }

    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的
     *
     * @param totalAudioLen  不包括header的音频数据总长度
     * @param longSampleRate 采样率,也就是录制时使用的频率
     * @param channels       audioRecord的频道数量
     */
    private fun generateWavFileHeader(totalAudioLen: Long, longSampleRate: Long, channels: Int): ByteArray {
        val totalDataLen = totalAudioLen + 36
        val byteRate = longSampleRate * 2 * channels.toLong()
        val header = ByteArray(44)
        header[0] = 'R'.toByte() // RIFF
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()//数据大小
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()//WAVE
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        //FMT Chunk
        header[12] = 'f'.toByte() // 'fmt '
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()//过渡字节
        //数据大小
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        //编码方式 10H为PCM编码格式
        header[20] = 1 // format = 1
        header[21] = 0
        //通道数
        header[22] = channels.toByte()
        header[23] = 0
        //采样率，每个通道的播放速度
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (2 * channels).toByte()
        header[33] = 0
        //每个样本的数据位数
        header[34] = 16
        header[35] = 0
        //Data chunk
        header[36] = 'd'.toByte()//data
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        return header
    }
}