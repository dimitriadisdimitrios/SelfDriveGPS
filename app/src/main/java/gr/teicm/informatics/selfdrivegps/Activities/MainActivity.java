package gr.teicm.informatics.selfdrivegps.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogShowAccount;
import gr.teicm.informatics.selfdrivegps.Fragment.DialogLogIn;
import gr.teicm.informatics.selfdrivegps.R;

public class MainActivity extends AppCompatActivity {
    private final static String VERSION_OF_APP = "v0.84";
    private Controller controller = new Controller();

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

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        loadPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RetrieveDataActivity.class));
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
                    dialogShowAccount.setCancelable(false);

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
}
