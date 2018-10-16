package com.video.raqust.myvideo.utils

import android.os.Environment
import java.io.File

/**
 * Created by linzehao
 * time: 2018/10/16.
 * info:
 */
object PathUtil {

    private const val PCM_FOLDER_NAME = "PCM"
    private const val WAVE_FOLDER_NAME = "WAVE"
    private const val MP4_FOLDER_NAME = "MP4"

    private val mRootPath by lazy { Environment.getExternalStorageDirectory()?.absolutePath + File.separator }

    fun getWaveFolderPath(): String {
        return mRootPath + WAVE_FOLDER_NAME
    }

    fun getCachePCMFolderPath(): String {
        return mRootPath + PCM_FOLDER_NAME
    }

    fun getMP4FolderPath1(): String {
        return mRootPath + MP4_FOLDER_NAME
    }
}