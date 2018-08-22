package gr.teicm.informatics.selfdrivegps.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogShowAccount;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogLogIn;
import gr.teicm.informatics.selfdrivegps.R;

public class MainActivity extends AppCompatActivity {
    private final static String VERSION_OF_APP = "v0.88";
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
        ImageButton iBtnLogIn = findViewById(R.id.iBtn_account_log_in);

        //Set the version of App on this variable
        tvVersionOfApp.setText(VERSION_OF_APP);

        counterToRefreshAccount(iBtnLogIn);

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
                    controller.setAppFragmentManager(getFragmentManager());
                    DialogLogIn dialogLogIn = new DialogLogIn();
                    dialogLogIn.show(getFragmentManager(), "Log In");
                    dialogLogIn.setCancelable(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
//        super.onBackPressed();
    }

    private void counterToRefreshAccount(final ImageButton imageButton){
        runnableForAccountIcon = new Runnable() {
            @Override
            public void run() {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        imageButton.setImageResource(R.drawable.green_log_in);
                    }else{
                        imageButton.setImageResource(R.drawable.user_log_in);
                    }
                handler.postDelayed(runnableForAccountIcon, 500);
            }
        };
        handler.postDelayed(runnableForAccountIcon, 500);
    }
}
