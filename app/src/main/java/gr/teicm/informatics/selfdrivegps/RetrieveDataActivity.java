package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Utilities.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.MapsUtilities;

public class RetrieveDataActivity extends Activity {
    final String TAG = "RetrieveDataActivity";

    private ArrayList<String> fList = new ArrayList<>();
    private ArrayList<LatLng> points = new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);
        context = this.getApplicationContext();


        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                //Get child names from FireBase to show it on ListView
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    fList.add(id);
                }

                //Create ListView to show data from FireBase
                ListView listView =  findViewById(R.id.list_view_main_frame);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view, R.id.list_view_sample, fList);
                listView.setAdapter(adapter);

                //When you click Items on ListView it send you to maps Activity and make buttons there, invisible
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String childName = (String) adapterView.getItemAtPosition(i);
                        Controller.setIdOfListView(childName);

                        for (DataSnapshot childCount: dataSnapshot.child(childName).getChildren()) {
                            Double latitude = childCount.child("latitude").getValue(Double.class);
                            Double longitude = childCount.child("longitude").getValue(Double.class);
                            if(latitude!=null && longitude!=null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                points.add(latLng);
                            }
                        }
                        Intent strMaps = new Intent(context, MapsActivity.class);
                        strMaps.putExtra("buttonStatus", "invisible");
                        strMaps.putParcelableArrayListExtra("latLng", points);
                        context.startActivity(strMaps);
                        }
                    });
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "DatabaseError"+databaseError.getCode());
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}