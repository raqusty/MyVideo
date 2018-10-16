package com.video.raqust.myvideo.fourthSection;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.video.raqust.myvideo.R;
import com.video.raqust.myvideo.utils.PathUtil;

import java.io.File;
import java.util.Calendar;

public class ShootingActivity extends Activity implements
        SurfaceHolder.Callback, OnClickListener {

    private static final String TAG = "MainActivity";
    private final int RESULT_SHOOTNG = 4;
    private SurfaceView mSurfaceview;
    private Button mBtnsend;
    private Button mStartStop;
    private Button mPlay;
    private boolean mIsPlay = false;// 是否正在播放录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path = null;
    private TextView mTimeTV;
    private int time = 0;

    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time++;
            mTimeTV.setText(time + "");
            handler.postDelayed(this, 1000);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shooting);

        inIt();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.btnsend:
                Intent intent = new Intent();
                intent.putExtra("data", path);
                setResult(-1, intent);
                finish();
                break;
            case R.id.btnStartStop:
                startShooting();
                break;
            case R.id.btnPlayVideo:
                StopShooting();
                playVideo(path);
                break;
            default:
                break;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1,
                               int i2) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void inIt() {
        // TODO Auto-generated method stub

        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mBtnsend = (Button) findViewById(R.id.btnsend);
        mBtnsend.setOnClickListener(this);
        mTimeTV = (TextView) findViewById(R.id.time);

        mPlay = (Button) findViewById(R.id.btnPlayVideo);
        mPlay.setOnClickListener(this);

        mStartStop = (Button) findViewById(R.id.btnStartStop);
        mStartStop.setOnClickListener(this);

        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 开始播放
     */
    private void playVideo(String videoPath) {

        mIsPlay = true;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        Uri uri = Uri.parse(videoPath);
        mediaPlayer = MediaPlayer.create(ShootingActivity.this, uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);//设置重复播放
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {

        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR); // 获取年份
        int month = ca.get(Calendar.MONTH); // 获取月份
        int day = ca.get(Calendar.DATE); // 获取日
        int minute = ca.get(Calendar.MINUTE); // 分
        int hour = ca.get(Calendar.HOUR); // 小时
        int second = ca.get(Calendar.SECOND); // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }

    /**
     * 开始录制
     */
    private void startShooting() {

        mTimeTV.setVisibility(View.VISIBLE);
        mBtnsend.setVisibility(View.INVISIBLE);

        if (mIsPlay) {
            if (mediaPlayer != null) {
                mIsPlay = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        handler.postDelayed(runnable, 1000);
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            camera.setDisplayOrientation(90);
            camera.unlock();
            mRecorder.setCamera(camera);
        }

        try {
            // 这两项需要放在setOutputFormat之前
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 这两项需要放在setOutputFormat之后
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            mRecorder.setVideoSize(640, 480);
            mRecorder.setVideoFrameRate(30);
            mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            mRecorder.setOrientationHint(90);
            // 设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(10 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

            path = PathUtil.INSTANCE.getMP4FolderPath();
            if (path != null) {
                File dir = new File(path + "/recordtest");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                path = dir + "/" + getDate() + ".mp4";
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    private void StopShooting() {

        mTimeTV.setVisibility(View.INVISIBLE);
        mBtnsend.setVisibility(View.VISIBLE);
        // stop
        try {
            handler.removeCallbacks(runnable);
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            time = 0;
            if (camera != null) {
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新录制
     */
    private void resetShooting() {

        time = 0;
        mTimeTV.setText(time + "");
        mTimeTV.setVisibility(View.VISIBLE);
        mBtnsend.setVisibility(View.INVISIBLE);
        if (mIsPlay) {
            if (mediaPlayer != null) {
                mIsPlay = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (null == camera) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(mSurfaceHolder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}
