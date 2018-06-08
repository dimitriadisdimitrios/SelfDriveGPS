package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

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

                //Get child names from FireBase to show it on ListView
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    fList.add(id);
                }
                //TODO: Check warning!!!
                //Retrieve data from fireBase and place it on Map and then ArrayLis
                Map<String, LatLng> baseRetrievedData = (Map<String, LatLng>) dataSnapshot.getValue();
                ArrayList<LatLng> listOfLatLng = new ArrayList<>();
                if(baseRetrievedData != null) {
                    listOfLatLng.addAll(baseRetrievedData.values());
                }
                else{
                    Log.d(TAG, "Attempt to invoke interface method 'java.util.Collection java.util.Map.values()' on a null object reference");
                }
                for(int i=0;i<listOfLatLng.size();i++) {
                    Log.d(TAG, String.valueOf(listOfLatLng.get(i)));
                }

                //Create ListView to show data from FireBase
                ListView listView = (ListView) findViewById(R.id.list_view_main_frame);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view, R.id.list_view_sample, fList);
                listView.setAdapter(adapter);
                //When you click Items on ListView it send you to maps Activity and make buttons there, invisible
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String childName = (String) adapterView.getItemAtPosition(i);
                        Toast.makeText(getApplicationContext(), childName,Toast.LENGTH_LONG).show();
                        Intent strMaps = new Intent(RetrieveDataActivity.this, MapsActivity.class);
                        strMaps.putExtra("buttonStatus", "invisible");
                        RetrieveDataActivity.this.startActivity(strMaps);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}