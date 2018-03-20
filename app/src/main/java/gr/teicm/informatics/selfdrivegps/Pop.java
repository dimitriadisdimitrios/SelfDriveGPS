package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Pop extends Activity {

    public  EditText collectionOfLatLng;
    public  Button sendToFireBase;
    public  Context context = null;

    private static String nameOfDataBaseKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activilty_pop);

        context = getApplicationContext();

        //TODO: Check if pop window can be more automated
        //Pop screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.7),(int)(height*0.22));

        //TODO: Check if i stay still the arrayList doesn't keep the same LatLng multiple times
        //ArrayList from MapsActivity class
        final ArrayList<LatLng> mNewPoints = (ArrayList<LatLng>) getIntent().getSerializableExtra("ArrayList");

        //Set FireBase Database
        final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();

        //button Function
        sendToFireBase = (Button) findViewById(R.id.send_group_to_fireBase);
        collectionOfLatLng = (EditText) findViewById(R.id.pop_editText);

        //initialize nameOfDataBaseKey

        sendToFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameOfDataBaseKey = collectionOfLatLng.getText().toString();

                if(!nameOfDataBaseKey.matches(""))
                {
                    myRef1.setValue(mNewPoints);
                    Toast.makeText(context, "LatLng have been added", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(context, "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}