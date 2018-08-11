package gr.teicm.informatics.selfdrivegps.Activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import gr.teicm.informatics.selfdrivegps.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ToggleButton tBtnWifi =  findViewById(R.id.tBtn_wifi);
        final ToggleButton tBtnBluetooth =  findViewById(R.id.tBtn_bluetooth);
        TextView tvBluetooth = findViewById(R.id.tv_bluetooth);

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(wifiManager!=null) {
            if (wifiManager.isWifiEnabled()) {
                tBtnWifi.setChecked(true);
            }

            if(bluetoothAdapter==null){ //If console doesn't support bluetooth make invisible the toggleBtn and  textView
                tBtnBluetooth.setVisibility(View.INVISIBLE);
                tvBluetooth.setVisibility(View.INVISIBLE);
            }
            else if(bluetoothAdapter.isEnabled()){
                tBtnBluetooth.setChecked(true);
            }
        }

        tBtnWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) { //What happen when toggleBtn pressed
                if(isChecked && wifiManager!=null){
                    wifiManager.setWifiEnabled(true);
                }else if(!isChecked && wifiManager!=null){
                    wifiManager.setWifiEnabled(false);
                }
            }
        });

        tBtnBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) { //What happen when toggleBtn pressed
                if(isChecked && bluetoothAdapter!=null){
                    bluetoothAdapter.enable();
                }else if(!isChecked && bluetoothAdapter!=null){
                    bluetoothAdapter.disable();
                }
            }
        });
    }
}
