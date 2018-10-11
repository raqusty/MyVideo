package com.video.raqust.myvideo.secondSection

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.video.raqust.myvideo.R
import kotlinx.android.synthetic.main.activity_second_section.*


class SecondSectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_section)

        VideoUtil.init(this.application)

        VideoUtil.setOnStateListener(object : VideoUtil.OnState {
            override fun onStateChanged(currentState: VideoUtil.WindState) {
                Log.i("linzehao", "currentState $currentState")
            }
        })

        text1.setOnClickListener {
            VideoUtil.startRecord(true)
        }

        text2.setOnClickListener {
            VideoUtil.stopRecord()
        }

        text3.setOnClickListener {
            VideoUtil.startPlayWav()
        }

        text4.setOnClickListener {
            VideoUtil.startPlayPCM()
        }

        text5.setOnClickListener {
            VideoUtil.stopPlay()
        }

        text6.setOnClickListener {

        }
    }
}
