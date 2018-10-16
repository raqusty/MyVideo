package com.video.raqust.myvideo.fourthSection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.video.raqust.myvideo.R
import kotlinx.android.synthetic.main.activity_fourth_section_media_recorder.*

/**
 * Created by linzehao
 * time: 2018/10/16.
 * info:
 */
class FourthSectionActivity : AppCompatActivity() {
    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth_section_media_recorder)

        mContext = this

        text1.setOnClickListener {
            val intent = Intent(mContext, MediaRecorderActivity::class.java)
            startActivity(intent)
        }

        text2.setOnClickListener {
        }

    }
}