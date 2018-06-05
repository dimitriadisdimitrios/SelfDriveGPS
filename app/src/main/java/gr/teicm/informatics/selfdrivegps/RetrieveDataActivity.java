package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RetrieveDataActivity extends Activity {
    final String TAG = "RetrieveDataActivity";

    private ArrayList<String> fList = new ArrayList<>();

    //TODO: Create a list view with name of keys
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    if(id!=null){
                        fList.add(id);
                    }
                }
                ListView listView = (ListView) findViewById(R.id.list_view_main_frame);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view, R.id.list_view_sample, fList);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        //TODO: Revision layout page
        Button startMapsWithDataBtn = (Button) findViewById(R.id.send_data_to_mapsActivity_btn);
        startMapsWithDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent strMaps = new Intent(RetrieveDataActivity.this, MapsActivity.class);
                RetrieveDataActivity.this.startActivity(strMaps);
            }
        });

//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        rootRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //Retrieve data from fireBase and place it on Map and then ArrayLis
//                //TODO: Check warning!!!
//        //TODO: Retrive Lat-Log from FireBase and place it on ArrayList<LatLng>
//                Map<String, LatLng> baseRetrievedData = (Map<String, LatLng>) dataSnapshot.getValue();
//                ArrayList<LatLng> listOfLatLng = new ArrayList<>();
//
//                if(baseRetrievedData != null) {
//                    listOfLatLng.addAll(baseRetrievedData.values());
//
//                }
//                else{
//                    Log.d(TAG, "Attempt to invoke interface method 'java.util.Collection java.util.Map.values()' on a null object reference");
//                }
//
    }
    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
//        super.onBackPressed();
    }
}