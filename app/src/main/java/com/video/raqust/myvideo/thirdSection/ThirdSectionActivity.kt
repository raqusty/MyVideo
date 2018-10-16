package com.video.raqust.myvideo.thirdSection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.video.raqust.myvideo.R
import kotlinx.android.synthetic.main.activity_third_section.*

/**
 * Created by linzehao
 * time: 2018/10/15.
 * info:
 */
class ThirdSectionActivity : AppCompatActivity() {
    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_section)

        mContext = this

        text1.setOnClickListener {
            surface_view.startVideo()
            video_texture.stopVIdeo()
        }

        text2.setOnClickListener {
            video_texture.startVideo()
            surface_view.stopVIdeo()
        }

        text3.setOnClickListener {
            val intent = Intent(mContext, Nv21VideoActivity::class.java)
            startActivity(intent)
        }
    }
}