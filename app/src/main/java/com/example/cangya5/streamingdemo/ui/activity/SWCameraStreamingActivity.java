package com.example.cangya5.streamingdemo.ui.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import com.example.cangya5.streamingdemo.R;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SWCameraStreamingActivity extends Activity
        implements StreamingStateChangedListener {

    @BindView(R.id.cameraPreview_surfaceView)
    GLSurfaceView cameraPreviewSurfaceView;
    @BindView(R.id.cameraPreview_afl)
    AspectFrameLayout cameraPreviewAfl;

    private JSONObject mJSONObject;
    private MediaStreamingManager mMediaStreamingManager;
    private StreamingProfile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swcamera_streaming);
        ButterKnife.bind(this);
        initViews();
        setEvent();
    }

    private void setEvent() {
        String streamJsonStrFromServer = getIntent().getStringExtra("stream_json_str");
        try {
            mJSONObject = new JSONObject(streamJsonStrFromServer);
            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
            mProfile = new StreamingProfile();
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                    .setStream(stream);  // You can invoke this before startStreaming, but not in initialization phase.
            CameraStreamingSetting setting = new CameraStreamingSetting();
            setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                    .setContinuousFocusModeEnabled(true)
                    .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                    .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);

            //构造带有视频 CameraStremaingManager,需要传入AspectFrameLayout和GLSurfaceView,第四个参数设置编码的方式，硬编or软编
            mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewAfl, cameraPreviewSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
            mMediaStreamingManager.prepare(setting, mProfile);
            //推流状态回调监听,比如流断开,重连回调
            mMediaStreamingManager.setStreamingStateListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        //SHOW_MODE.FULL，可以全屏显示（没有黑边），但是预览的图像和播放的效果有出入,即之前的黑边部分其实没有推流
        //SHOW_MODE.REAL，所见即所得
        cameraPreviewAfl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
    }

    @OnClick({R.id.cameraPreview_surfaceView, R.id.cameraPreview_afl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cameraPreview_surfaceView:
                break;
            case R.id.cameraPreview_afl:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // You must invoke pause here.
        mMediaStreamingManager.pause();
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case PREPARING:
                break;
            case READY:
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaStreamingManager != null) {
                            mMediaStreamingManager.startStreaming();
                        }
                    }
                }).start();
                break;
            case CONNECTING:
                break;
            case STREAMING:
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                // The streaming had been finished.
                break;
            case IOERROR:
                // Network connect error.
                break;
            case OPEN_CAMERA_FAIL:
                // Failed to open camera.
                break;
            case DISCONNECTED:
                // The socket is broken while streaming
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
