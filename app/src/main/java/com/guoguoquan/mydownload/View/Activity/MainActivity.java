package com.guoguoquan.mydownload.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.guoguoquan.mydownload.Bean.FileInfo;
import com.guoguoquan.mydownload.R;
import com.guoguoquan.mydownload.View.Service.MyDownloadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartDownButton;
    private Button mPauseDownButton;

    private FileInfo fileInfo = new FileInfo(0, "http://down.mumayi.com/995595", "lock.apk", 0, 0);
private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartDownButton = (Button) findViewById(R.id.bt_start_down);
        mPauseDownButton = (Button) findViewById(R.id.bt_pause_down);
        mStartDownButton.setOnClickListener(this);
        mPauseDownButton.setOnClickListener(this);
        intent = new Intent(MainActivity.this, MyDownloadService.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_down:
                intent.setAction(MyDownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
                break;
            case R.id.bt_pause_down:
                intent.setAction(MyDownloadService.ACTION_STOP);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
                break;
        }
    }
}
