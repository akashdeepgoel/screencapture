package com.eecs.screencapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button viewScreenshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewScreenshots = (Button) findViewById(R.id.viewScreenshots);
        viewScreenshots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivity(intent);
            }
        });
    }
}
