package gr.teicm.informatics.selfdrivegps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String VERSION_OF_APP = "v0.71";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn =  findViewById(R.id.start_calculations_btn);
        Button loadPlanBtn =  findViewById(R.id.load_plans_btn);
        Button settingBtn =  findViewById(R.id.setting_btn);
        TextView tvVersionOfApp = findViewById(R.id.tv_app_version);

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
    }

    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
//        super.onBackPressed();
    }
}
