package com.antivirus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class NetworkSetting extends AppCompatActivity {
    SwitchCompat networkBlockerToggle;
    TextView networkBlockerText;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    View pinLayout;
    AppCompatEditText confirmPin;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_setting);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        builder = new AlertDialog.Builder(this);

        networkBlockerToggle = findViewById(R.id.switchToggleNetwork);
        networkBlockerText = findViewById(R.id.networkBlockerText);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE) ;
        pinLayout = inflater.inflate(R.layout.set_pin, null);
        pinLayout.findViewById(R.id.oldPin).setVisibility(View.GONE);
        confirmPin = pinLayout.findViewById(R.id.confirmPIN);
        pinLayout.findViewById(R.id.newPin).setVisibility(View.GONE);
        //check if pin already set
        if(pref.getString("networkBlockerEnabled","off").equalsIgnoreCase("on")){
           enableFormat();
        }
        else{
           disableFormat();
        }
        networkBlockerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show changing old PIN dialog
                if(networkBlockerToggle.isChecked()){
                    showDialogForNetwork("Enable Network Blocker", "enable");
                }
                else{
                    showDialogForNetwork("Disable Network Blocker", "disable");
                }
            }
        });

    }
    private void enableFormat(){
        networkBlockerToggle.setText("Disable Network Blocker");
        networkBlockerToggle.setChecked(true);
        networkBlockerText.setText("Disabling will not block network access by any application");
    }
    private void disableFormat() {
        networkBlockerToggle.setText("Enable Network Blocker");
        networkBlockerToggle.setChecked(false);
        networkBlockerText.setText("Allow network access to selected apps only, and by default block network access for any new installed application");
    }
    private void showDialogForNetwork(String title, String type) {
        builder.setTitle(title);
        builder.setCancelable(true);
        confirmPin.setText("");
        if (pinLayout.getParent() != null)
            ((ViewGroup) pinLayout.getParent()).removeView(pinLayout);
        if(type.equalsIgnoreCase("disable"))
            builder.setMessage("This will disable the network blocker feature and all applications will be able to access network. You sure want to DISABLE??");
        else{
            builder.setMessage("Enabling this feature will block the network access for all applications and allow network access for selected application only.");
        }
        if(!pref.getString("password", "").isEmpty()){
            builder.setView(pinLayout);
        }

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
                public void onCancel(DialogInterface dialog) {
                    if(type.equalsIgnoreCase("disable")) {
                       disableFormat();
                    }
                    else {
                        enableFormat();
                    }
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(type.equalsIgnoreCase("disable")) {
                        disableFormat();
                    }
                    else {
                        enableFormat();
                    }
                }
            });
            builder.setPositiveButton(type.toUpperCase(Locale.ROOT), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //shared pref to check the pin
                        if (type.equalsIgnoreCase("disable")) {
                            if (pref.getString("password", "").toString().equalsIgnoreCase(confirmPin.getText().toString())) {
                                disableFormat();
                                editor.putString("networkBlockerEnabled","off");
                                editor.commit();
                            } else {
                                enableFormat();
                                editor.putString("networkBlockerEnabled","on");
                                editor.commit();
                            }
                        } else if (type.equalsIgnoreCase("enable")) {
                            if (pref.getString("password", "").toString().equalsIgnoreCase(confirmPin.getText().toString())) {
                                enableFormat();
                                editor.putString("networkBlockerEnabled","on");
                                editor.commit();
                            } else {
                                disableFormat();
                                editor.putString("networkBlockerEnabled","off");
                                editor.commit();
                            }
                        }
                    }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }

}