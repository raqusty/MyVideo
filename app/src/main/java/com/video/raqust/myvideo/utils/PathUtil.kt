package com.video.raqust.myvideo.utils

import android.os.Environment
import java.io.File

/**
 * Created by linzehao
 * time: 2018/10/16.
 * info:
 */
object PathUtil {
    private const val VIDEO = "A_VIDEO"

    private val PCM_FOLDER_NAME by lazy { VIDEO + File.separator + "PCM" }
    private val WAVE_FOLDER_NAME by lazy { VIDEO + File.separator + "WAVE" }
    private val MP4_FOLDER_NAME by lazy { VIDEO + File.separator + "MP4" }

    private val mRootPath by lazy { Environment.getExternalStorageDirectory()?.absolutePath + File.separator }

    fun getWaveFolderPath(): String {
        createFoleder(mRootPath + WAVE_FOLDER_NAME)
        return mRootPath + WAVE_FOLDER_NAME
    }

    fun getCachePCMFolderPath(): String {
        createFoleder(mRootPath + PCM_FOLDER_NAME)
        return mRootPath + PCM_FOLDER_NAME
    }

    fun getMP4FolderPath(): String {
        createFoleder(mRootPath + MP4_FOLDER_NAME)
        return mRootPath + MP4_FOLDER_NAME
    }

    private fun createFoleder(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }
}