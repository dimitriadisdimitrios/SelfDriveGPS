package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Dimitriadis983 on 13-Mar-18.
 */

public class Pop extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activilty_pop);

        //pop screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.7),(int)(height*0.22));

        final ArrayList<LatLng> mNewPoints = (ArrayList<LatLng>) getIntent().getSerializableExtra("ArrayList");


        //Set FireBase Database
        final DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();

        //button Function
        Button sendToFireBase = (Button) findViewById(R.id.send_group_to_fireBase);
        sendToFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef1.setValue(mNewPoints);

            }
        });
//        EditText collectionOfLatLng = (EditText) findViewById(R.id.editText);


    }
}
