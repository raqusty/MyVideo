package com.video.raqust.myvideo.firstSection

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.video.raqust.myvideo.R
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_first_section.*


class FirstSectionActivity : AppCompatActivity() {

    private val  mBitmap: Bitmap by lazy {  BitmapFactory.decodeResource(resources, R.mipmap.first_section_picutre) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_section)

        initFirstPicture()
        initSecondPicture()
        initThirdPicture()
    }

    private fun initFirstPicture(){
        image1.setImageBitmap(mBitmap)
    }

    private fun initSecondPicture(){
        image2.setBitmap(mBitmap)
    }

    private fun initThirdPicture(){
        image3.setBitmap(mBitmap)
    }

}
