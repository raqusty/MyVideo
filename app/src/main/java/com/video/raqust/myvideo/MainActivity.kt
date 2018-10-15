package com.video.raqust.myvideo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.video.raqust.myvideo.firstSection.FirstSectionActivity
import com.video.raqust.myvideo.secondSection.SecondSectionActivity
import com.video.raqust.myvideo.thirdSection.ThirdSectionActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var mContext: Context ?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.RECORD_AUDIO), 1);
        }


        text1.setOnClickListener {
            val intent = Intent(mContext, FirstSectionActivity::class.java)
            startActivity(intent)
        }

        text2.setOnClickListener {
            val intent = Intent(mContext, SecondSectionActivity::class.java)
            startActivity(intent)
        }

        text3.setOnClickListener {
            val intent = Intent(mContext, ThirdSectionActivity::class.java)
            startActivity(intent)
        }

    }
}
