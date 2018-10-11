package com.video.raqust.myvideo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text1.setOnClickListener {
            val uri = Uri.parse("yys://test")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        text2.setOnClickListener {
            val uri = Uri.parse("yiyoushuo://test")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}
