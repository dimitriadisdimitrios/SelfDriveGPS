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

public class PopToFireBase extends Activity {

    //TODO: Check the definition of static and check of variables
    public static int width;
    public static int height;
    public String nameOfDataBaseKey;

    private EditText collectionOfLatLng;
    private Button sendToFireBase;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activilty_pop);

        context = getApplicationContext();

        //TODO: Check if pop window can be more automated
        //PopToFireBase screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.7),(int)(height*0.22));

        //TODO: Check if i stay still the arrayList doesn't keep the same LatLng multiple times
        //TODO: Check if o should use HashMap instead of ArrayList
        //ArrayList from MapsActivity class
        final ArrayList<LatLng> mNewPoints = (ArrayList<LatLng>) getIntent().getSerializableExtra("ArrayList");

        //TODO: Check if i can place on top 2 of this final variable
        //Set FireBase Database
        final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();

        //button Function
        sendToFireBase = (Button) findViewById(R.id.send_to_fireBase_btn);
        collectionOfLatLng = (EditText) findViewById(R.id.pop_name_DB_ET);

        sendToFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize nameOfDataBaseKey
                nameOfDataBaseKey = collectionOfLatLng.getText().toString();

                if(!nameOfDataBaseKey.matches(""))
                {
                    myRef1.child(nameOfDataBaseKey).setValue(mNewPoints);
                    Toast.makeText(context, "LatLng have been added", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(context, "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}