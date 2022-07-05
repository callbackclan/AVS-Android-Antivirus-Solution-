package com.antivirus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class PassCodeSetting extends AppCompatActivity {
    SwitchCompat enablePin;
    TextView changePin;
    TextView forgotPin;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    View pinLayout;
    AppCompatEditText oldPin, newPin, confirmPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code_setting);
        enablePin = findViewById(R.id.switchToggle);
        changePin = findViewById(R.id.ChangePinText);
        forgotPin = findViewById(R.id.forgotPinText);
        builder = new AlertDialog.Builder(this);

        changePin.setVisibility(View.GONE);
        forgotPin.setVisibility(View.GONE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE) ;
        pinLayout = inflater.inflate(R.layout.set_pin, null);
        oldPin = pinLayout.findViewById(R.id.oldPin);
        confirmPin = pinLayout.findViewById(R.id.confirmPIN);
        newPin = pinLayout.findViewById(R.id.newPin);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        //check if pin already set
        if(pref.getString("password","").isEmpty()){
            enablePin.setText("ENABLE PIN");
            enablePin.setChecked(false);
            changePin.setVisibility(View.GONE);
            forgotPin.setVisibility(View.GONE);
        }
        else{
            enablePin.setText("DISABLE PIN");
            enablePin.setChecked(true);
            changePin.setVisibility(View.VISIBLE);
            forgotPin.setVisibility(View.VISIBLE);
        }

        enablePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show changing old PIN dialog
                if(enablePin.isChecked()){
                    setEnablePin("SET PIN", 0);
                }
                else{
                    setDisablePin("You sure you want to disable Pin?");
                }
            }
        });
        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnablePin("Change Password", 1);
            }
        });
//        forgotPin.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //clear PIN from shared Prefs and set new pin
//                setEnablePin("Forgot Password", 2);
//            }
//        });
    }

    private void setEnablePin(String title, int type){
        builder.setTitle(title);
        builder.setCancelable(true);
        boolean state = enablePin.isChecked();
        if(type==0){
            oldPin.setVisibility(View.GONE);
        }
        else if(type==1){
            oldPin.setVisibility(View.VISIBLE);
        }
        newPin.setVisibility(View.VISIBLE);
        confirmPin.setVisibility(View.VISIBLE);
        setLayout();
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(type==0){
                    enablePin.setText("Enable PIN");
                    enablePin.setChecked(!state);
                    changePin.setVisibility(View.GONE);
                    forgotPin.setVisibility(View.GONE);
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //clear Shared Pref for pin
                if(type==0){
                    enablePin.setText("Enable PIN");
                    enablePin.setChecked(!state);
                    changePin.setVisibility(View.GONE);
                    forgotPin.setVisibility(View.GONE);
                }
            }
        });
        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cPin, nPin, oPin = "";
                nPin = newPin.getText().toString();
                cPin = confirmPin.getText().toString();
                //setting up new password
                if(type==0){
                    if (!corruptedPassword(nPin, newPin)) {
                        if(!corruptedPassword(cPin, confirmPin)){
                            if(cPin.equals(nPin)){
                                enablePin.setText("DISABLE PIN");
                                enablePin.setChecked(state);
                                changePin.setVisibility(View.VISIBLE);
                                forgotPin.setVisibility(View.VISIBLE);
                                editor.putString("password", nPin);
                                editor.commit();

                            }
                            else{
                                confirmPin.setError("Password Not Matching");
                                confirmPin.setText("");
                                errorAnimation(confirmPin);
                            }

                        }else{
                            confirmPin.setText("");
                            errorAnimation(confirmPin);
                        }
                    }else{
                        newPin.setText("");
                        confirmPin.setText("");
                        errorAnimation(newPin);
                    }
                }

                //change password
                else if(type==1){
                    oPin = oldPin.getText().toString();
                    if(!corruptedPassword(oPin, oldPin)){
                        if(oPin.equals(pref.getString("password", ""))){
                            if (!corruptedPassword(nPin, newPin)) {
                                if(!corruptedPassword(cPin, confirmPin)){
                                    if(cPin.equals(nPin)){
                                        enablePin.setText("DISABLE PIN");
                                        enablePin.setChecked(state);
                                        changePin.setVisibility(View.VISIBLE);
                                        forgotPin.setVisibility(View.VISIBLE);
                                        editor.putString("password", nPin);
                                        editor.commit();
                                    }
                                    else{
                                        confirmPin.setError("Password Not Matching");
                                        confirmPin.setText("");
                                        errorAnimation(confirmPin);
                                    }

                                }else{
                                    confirmPin.setText("");
                                    errorAnimation(confirmPin);
                                }
                            }else{
                                newPin.setText("");
                                confirmPin.setText("");
                                errorAnimation(newPin);
                            }
                        }else{
                            //password not same
                            oldPin.setError("Please enter old password correctly");
                            oldPin.setText("");
                            newPin.setText("");
                            confirmPin.setText("");
                            errorAnimation(oldPin);
                        }
                    }
                    else{
                        oldPin.setText("");
                        newPin.setText("");
                        confirmPin.setText("");
                        errorAnimation(oldPin);
                    }

                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }
    private void setDisablePin(String title) {
        builder.setTitle(title);
        builder.setCancelable(true);
        boolean state = enablePin.isChecked();
        confirmPin.setVisibility(View.VISIBLE);
        oldPin.setVisibility(View.GONE);
        newPin.setVisibility(View.GONE);
        setLayout();

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enablePin.setText("DISABLE PIN");
                enablePin.setChecked(!state);
                changePin.setVisibility(View.VISIBLE);
                forgotPin.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enablePin.setText("DISABLE PIN");
                enablePin.setChecked(!state);
                changePin.setVisibility(View.VISIBLE);
                forgotPin.setVisibility(View.VISIBLE);
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //shared pref to delete pin
                if(!corruptedPassword(confirmPin.getText().toString(), confirmPin)){
                    if(pref.getString("password", "").equals(confirmPin.getText().toString())){
                        editor.putString("password", "");
                        editor.commit();
                        enablePin.setText("ENABLE PIN");
                        enablePin.setChecked(state);
                        changePin.setVisibility(View.GONE);
                        forgotPin.setVisibility(View.GONE);

                    }
                    else{
                        enablePin.setChecked(!state);
                        changePin.setVisibility(View.VISIBLE);
                        forgotPin.setVisibility(View.VISIBLE);
                    }
                }

            }

        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean corruptedPassword(String password, AppCompatEditText view){
        boolean error = false;
        if (password.isEmpty()) {
            view.setError("Required");
            error = true;
        }else if (!(password.length() == 4)) {
            view.setError("Please Enter 4 digit password");
            error = true;
        } else if (password.contains(" ")) {
            view.setError("Space not allowed");
            error = true;
        }
        return error;


    }

    private void errorAnimation(View view){
        Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
        return;
    }
    private void setLayout(){
        this.oldPin.setText("");
        this.confirmPin.setText("");
        this.newPin.setText("");
        if(pinLayout.getParent()!=null)
            ((ViewGroup)pinLayout.getParent()).removeView(pinLayout);
        builder.setView(pinLayout);
    }

}