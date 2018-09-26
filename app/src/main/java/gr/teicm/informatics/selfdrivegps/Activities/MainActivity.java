package gr.teicm.informatics.selfdrivegps.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogShowAccount;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogLogIn;
import gr.teicm.informatics.selfdrivegps.R;

public class MainActivity extends AppCompatActivity {
    private final static String VERSION_OF_APP = "v0.909";
    private Controller controller = new Controller();
    private static Handler handler = new Handler();
    private static Runnable runnableForAccountIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn =  findViewById(R.id.start_calculations_btn);
        Button loadPlanBtn =  findViewById(R.id.load_plans_btn);
        Button settingBtn =  findViewById(R.id.setting_btn);
        TextView tvVersionOfApp = findViewById(R.id.tv_app_version);
        LinearLayout llLocationModeWarning = findViewById(R.id.ll_warning_about_location_mode);
        ImageButton iBtnLogIn = findViewById(R.id.iBtn_account_log_in);

        //Set the version of App on this variable
        tvVersionOfApp.setText(VERSION_OF_APP);
        //Set fragmentManager on controller to open dialogs
        controller.setAppFragmentManager(getFragmentManager());

        counterToRefreshAccount(iBtnLogIn, llLocationModeWarning);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    handler.removeCallbacks(runnableForAccountIcon);
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "You need to Log-In, first !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    handler.removeCallbacks(runnableForAccountIcon);
                    startActivity(new Intent(MainActivity.this, RetrieveDataActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "You need to Log-In, first !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        iBtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    DialogShowAccount dialogShowAccount = new DialogShowAccount();
                    dialogShowAccount.show(getFragmentManager(),"Show Account");
                }else{
                    DialogLogIn dialogLogIn = new DialogLogIn();
                    dialogLogIn.show(getFragmentManager(), "Log In");
                    dialogLogIn.setCancelable(false);
                }
            }
        });

        //Implement SharedPreferences to get the last value of rangeMeter before app stop on previous use
        //to use it as implementation in whole app through controller
        SharedPreferences spf = getSharedPreferences("pref", MODE_PRIVATE);
        if(spf.contains("rangeMeterValue")){
            controller.setMeterOfRange(spf.getInt("rangeMeterValue", 8));
        }
    }

    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
        super.onBackPressed();
    }

    private void counterToRefreshAccount(final ImageButton imageButton, final LinearLayout tvLocationWarning){
        runnableForAccountIcon = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        imageButton.setImageResource(R.drawable.green_log_in);
                    }else{
                        imageButton.setImageResource(R.drawable.user_log_in);
                    }

                try {
                    int idMode = getLocationMode();
                    if(idMode == 3){ // If Location mode is High Accuracy set it invisible
                        tvLocationWarning.setVisibility(View.INVISIBLE);
                    }else { //Else, show warning message
                        tvLocationWarning.setVisibility(View.VISIBLE);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(runnableForAccountIcon, 500);
            }
        };
        handler.postDelayed(runnableForAccountIcon, 500);
    }

    private int getLocationMode() throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
    }
}
