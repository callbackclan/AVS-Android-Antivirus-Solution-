package com.antivirus;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PassCode extends AppCompatActivity {


    EditText passwordview ;
    Button verify;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String password = prefs.getString("password", "");
            if (password.isEmpty()) {
                sendToMain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        passwordview = findViewById(R.id.password);
        verify = findViewById(R.id.verify);

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = passwordview.getText().toString();
                    boolean error = false;
                    if (password.isEmpty()) {
                        Toast.makeText(PassCode.this, "Required", Toast.LENGTH_SHORT).show();
                        error = true;
                    } else if (password.length() < 4 || password.length() > 4) {
                        Toast.makeText(PassCode.this, "Please Enter 4 digit pin", Toast.LENGTH_SHORT).show();
                        error = true;
                    } else if (password.contains(" ")) {
                        Toast.makeText(PassCode.this, "Does not support space", Toast.LENGTH_SHORT).show();
                        error = true;
                    } else if (!password.equals(pref.getString("password", ""))) {
                        error = true;
                        Toast.makeText(PassCode.this, "Wrong PIN", Toast.LENGTH_SHORT).show();

                    }


                    if (error) {
                        Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);
                        passwordview.setText("");
                        passwordview.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
                        return;
                    } else
                        sendToMain();
                }
            });
        }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String password  = defPref.getString("password", "");
        if (password.isEmpty()) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(PassCode.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}