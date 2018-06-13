package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RetrieveDataActivity extends Activity {
//    final String TAG = "RetrieveDataActivity";

    private ArrayList<String> fList = new ArrayList<>();

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
                //Retrieve data from fireBase and place it on Map and then ArrayLis
//                Map<String, LatLng> baseRetrievedData = (Map<String, LatLng>) dataSnapshot.getValue();
//                ArrayList<LatLng> listOfLatLng = new ArrayList<>();

                //Create ListView to show data from FireBase
                ListView listView = (ListView) findViewById(R.id.list_view_main_frame);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view, R.id.list_view_sample, fList);
                listView.setAdapter(adapter);

                //When you click Items on ListView it send you to maps Activity and make buttons there, invisible
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String childName = (String) adapterView.getItemAtPosition(i);
                        new LatLngCollection(getApplicationContext()).execute(childName);
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