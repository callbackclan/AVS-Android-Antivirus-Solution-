package com.antivirus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    CardView profile;
    CardView passCodeSetting;
    CardView networkSetting;
    CardView profileSetting;
    AppCompatImageView profileImage;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        profile = findViewById(R.id.profileSetting);
        passCodeSetting = findViewById(R.id.passCodeSetting);
        profileImage = findViewById(R.id.profilePicture);
        networkSetting = findViewById(R.id.networkBlockerSetting);
        profileSetting = findViewById(R.id.profileSetting);
        fab = findViewById(R.id.fab);
        passCodeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PassCodeSetting.class));
            }
        });
        networkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, NetworkSetting.class));
            }
        });
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SignInActivity.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(SettingActivity.this)
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .saveDir(new File(getFilesDir(), "ImagePicker"))
                        .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        File file = new File(this.getFilesDir().toString()+"profile.jpeg");
        profileImage.setImageURI(uri);
    }
}