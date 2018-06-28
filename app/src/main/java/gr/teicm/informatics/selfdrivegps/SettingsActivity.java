package gr.teicm.informatics.selfdrivegps;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {
//    private String TAG = "SettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ToggleButton tBtnWifi =  findViewById(R.id.tBtn_wifi);
        ToggleButton tBtnBluetooth =  findViewById(R.id.tBtn_bluetooth);
        ToggleButton tBtnLocation =  findViewById(R.id.tBtn_gps);

        tBtnWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(isChecked && wifiManager!=null){
                    wifiManager.setWifiEnabled(true);
                }else if(!isChecked && wifiManager!=null){
                    wifiManager.setWifiEnabled(false);
                }
            }
        });

        tBtnBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(isChecked && bluetoothAdapter!=null) {
                    bluetoothAdapter.enable();
                }else if(!isChecked && bluetoothAdapter!=null){
                    bluetoothAdapter.disable();
                }
            }
        });

        tBtnLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        });
    }
}
