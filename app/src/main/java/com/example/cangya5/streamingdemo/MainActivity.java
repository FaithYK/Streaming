package com.example.cangya5.streamingdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cangya5.streamingdemo.ui.activity.SWCameraStreamingActivity;
import com.example.cangya5.streamingdemo.utils.ToastUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Intent intent;
    @BindView(R.id.put_streaming)
    Button putStreaming;

    private String requestStreamJson() {
        try {
            // Replace "Your app server" by your app sever url which can get the StreamJson as the SDK's input.
            HttpURLConnection httpConn = (HttpURLConnection) new URL("Your app server").openConnection();
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(10000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int length = httpConn.getContentLength();
            if (length <= 0) {
                return null;
            }
            InputStream is = httpConn.getInputStream();
            byte[] data = new byte[length];
            int read = is.read(data);
            is.close();
            if (read <= 0) {
                return null;
            }
            return new String(data, 0, read);
        } catch (Exception e) {
            ToastUtils.shortToast(MainActivity.this, getString(R.string.network_error));
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.put_streaming)
    public void onViewClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get the stream json from http
                    String streamJson = requestStreamJson();
                    intent = new Intent(MainActivity.this, SWCameraStreamingActivity.class);
                    intent.putExtra("stream_json_str", streamJson);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
