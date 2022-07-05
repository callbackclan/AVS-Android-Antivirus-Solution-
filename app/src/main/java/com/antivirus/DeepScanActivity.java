package com.antivirus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeepScanActivity extends AppCompatActivity {
    Button manualSelectButton, manualStartScanButton, automaticStartScanButton;
    TextView manualSelectedFile;
    Uri uri;
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_scan);
        manualSelectButton = findViewById(R.id.manualSelectButton);
        manualStartScanButton = findViewById(R.id.manualScanStartButton);
        manualSelectedFile = findViewById(R.id.manualSelectedFileName);
        automaticStartScanButton = findViewById(R.id.fullScanStartButton);
        manualSelectedFile.setVisibility(View.GONE);
        manualSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>23){
                    if(Util.checkPermission(DeepScanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        filePicker();
                    }
                    else{
                        Util.requestPermission(DeepScanActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                    }
                }
                else {
                    filePicker();
                }
            }
        });
        manualStartScanButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(data==null)
                    Toast.makeText(DeepScanActivity.this, "Please select the file", Toast.LENGTH_SHORT).show();
                else
                    uploadFile();
            }
        });



    }
    private void filePicker(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent = Intent.createChooser(intent, "Choose a File");
        sActivityResult.launch(intent);
    }


    ActivityResultLauncher<Intent> sActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK ){
                data = result.getData();
                manualSelectedFile.setVisibility(View.VISIBLE);
                assert data != null;
                manualSelectedFile.setText(PathUtils.getFileName(DeepScanActivity.this, data.getData()));

            }
        }
    });
    private void uploadFile(){
        String filePath =  PathUtils.getPath(DeepScanActivity.this, data.getData());
        PostHttp postHttp;
        if(filePath!=null){
            postHttp = new PostHttp(DeepScanActivity.this, filePath);
            String httpResponse = postHttp.startUploading();
            Log.d("test_",httpResponse);
        }
        else
            Toast.makeText(DeepScanActivity.this, "Cannot scan this file", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Util.onPermissionGranted(DeepScanActivity.this, requestCode, grantResults);
    }




}