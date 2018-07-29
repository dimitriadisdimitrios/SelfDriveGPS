package gr.teicm.informatics.selfdrivegps;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;

public class SettingsActivity extends AppCompatActivity {

    Controller controller = new Controller();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ToggleButton tBtnWifi =  findViewById(R.id.tBtn_wifi);
        final ToggleButton tBtnBluetooth =  findViewById(R.id.tBtn_bluetooth);
        Button btPlus = findViewById(R.id.btn_plus);
        Button btSub = findViewById(R.id.btn_sub);
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
        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 counterForRangeOfField("plus");
            }
        }); //Set listener for plus btn to increase the number
        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterForRangeOfField("sub");
            }
        });//Set listener for sub btn to increase the number
    }

    public void counterForRangeOfField(String function){
        TextView tvRangeOfField = findViewById(R.id.tv_range_of_field_meter);
        int counter = Integer.parseInt(tvRangeOfField.getText().toString());
        if(function.equals("plus")) {
            counter = counter + 1; //Increase the meter
        }else if(function.equals("sub")){
            counter = counter - 1; //Decrease the meter
        }
        controller.setMeterOfRange(counter); //Set counter to Controller
        tvRangeOfField.setText(getString(R.string.tv_meter_of_range_for_field,counter)); //Show counter to textView as result

    }
}
