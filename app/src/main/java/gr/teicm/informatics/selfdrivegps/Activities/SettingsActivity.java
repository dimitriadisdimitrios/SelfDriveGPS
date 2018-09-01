package gr.teicm.informatics.selfdrivegps.Activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;

public class SettingsActivity extends AppCompatActivity {
    private static Handler handler = new Handler();
    private static Runnable runnableForModes;
    private static Controller controller = new Controller();
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ToggleButton tBtnWifi =  findViewById(R.id.tBtn_wifi);
        final ToggleButton tBtnBluetooth =  findViewById(R.id.tBtn_bluetooth);
        TextView tvBluetooth = findViewById(R.id.tv_bluetooth);
        TextView tvLocationMode = findViewById(R.id.tv_location_mode);
        ImageButton iBtnLocationMode = findViewById(R.id.iBtn_location_mode);
        Button btPlus = findViewById(R.id.btn_plus);
        Button btSub = findViewById(R.id.btn_sub);
        final TextView tvRangeBetweenLines = findViewById(R.id.tv_range_of_field_meter);

        //Implement SharedPreferences to get the last value of rangeMeter before app stop on previous use
        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        if(sharedPreferences.contains("rangeMeterValue")){
            tvRangeBetweenLines.setText(getApplication().getString(R.string.tv_meter_of_range_for_field, sharedPreferences.getInt("rangeMeterValue", 8)));
        }


        counterToRefreshLocationMode(tvLocationMode);

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

        iBtnLocationMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open default Google Location page to change Mode
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        //For plus btn on range meter
        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterForRangeOfField("plus", tvRangeBetweenLines, getApplicationContext());
            }
        });
        //For sub btn on range meter
        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterForRangeOfField("sub", tvRangeBetweenLines, getApplicationContext());
            }
        });
    }

    private int getLocationMode() throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
    }

    private String foundWhichModeReturned(int id){
        switch (id){
            case 0:
                return "Location Mode: OFF";
            case 1:
                return "Location Mode: Sensors Only";
            case 2:
                return "Location Mode: Battery Saving";
            case 3:
                return "Location Mode: High Accuracy";
            default:
                return "Unknown Mode";
        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnableForModes);
        super.onBackPressed();
    }

    private void counterToRefreshLocationMode(final TextView textViewLocationMode){

        runnableForModes = new Runnable() {
            @Override
            public void run() {
                try { //On start of activity set on TextViewMode the current mode
                    int idMode = getLocationMode();
                    String modeName = foundWhichModeReturned(idMode);
                    textViewLocationMode.setText(modeName);
                }catch(Settings.SettingNotFoundException e){
                    e.printStackTrace();
                }
                handler.postDelayed(runnableForModes, 500);
            }
        };
        handler.postDelayed(runnableForModes, 500);
    }

    private static void counterForRangeOfField(String function, TextView tvRangeOfLines, Context context){
        int counter = Integer.parseInt(tvRangeOfLines.getText().toString());

        if(function.equals("plus")) {
            if(counter < 20){
                counter = counter + 1; //Increase the meter
            }
        }else if(function.equals("sub")){
            if(counter > 6) {
                counter = counter - 1; //Decrease the meter
            }
        }
        controller.setMeterOfRange(counter); //Set counter to Controller
        tvRangeOfLines.setText(context.getString(R.string.tv_meter_of_range_for_field,counter)); //Show counter to textView as result

        //Implement SharedPreferences to set value. So it can been accessed after a restart of app
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rangeMeterValue", counter);
        editor.apply();
    }
}
