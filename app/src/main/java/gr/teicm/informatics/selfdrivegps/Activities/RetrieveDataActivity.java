package gr.teicm.informatics.selfdrivegps.Activities;

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
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Controller.Controller;

public class RetrieveDataActivity extends Activity {
    final String TAG = "RetrieveDataActivity";

    private ArrayList<String> fList = new ArrayList<>();
    private ArrayList<LatLng> mPointsForField = new ArrayList<>();
    private ArrayList<LatLng> mPointsForLine = new ArrayList<>();
    private Controller controller = new Controller();
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
                ProgressBar progressBarOfWaitingFireBase = findViewById(R.id.pb_waiting_fireBase);
                progressBarOfWaitingFireBase.setVisibility(View.INVISIBLE);


                //Create ListView to show data from FireBase
                ListView listView =  findViewById(R.id.list_view_main_frame);
                listView.setVisibility(View.VISIBLE);
                listView.setClickable(true);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_view, R.id.list_view_sample, fList);
                listView.setAdapter(adapter);

                //When you click Items on ListView it send you to maps Activity and make buttons there, invisible
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String childName = (String) adapterView.getItemAtPosition(i);

                        for (DataSnapshot childCount: dataSnapshot.child(childName).child("Polygon").getChildren()) {
                            Double latitude = childCount.child("latitude").getValue(Double.class);
                            Double longitude = childCount.child("longitude").getValue(Double.class);
                            if(latitude!=null && longitude!=null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                mPointsForField.add(latLng);
                                controller.setArrayListForField(mPointsForField);
                            }
                        }
                        for (DataSnapshot childCount: dataSnapshot.child(childName).child("Polyline").getChildren()) {
                            Double latitude = childCount.child("latitude").getValue(Double.class);
                            Double longitude = childCount.child("longitude").getValue(Double.class);
                            if(latitude!=null && longitude!=null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                mPointsForLine.add(latLng);
                                controller.setArrayListForLine(mPointsForLine);
                            }
                        }

                        Integer rangeBetweenPolylines = dataSnapshot.child(childName).child("Meter").getValue(Integer.class);
                        if (rangeBetweenPolylines != null){
                            int rangeBetweenLines = rangeBetweenPolylines;
                            controller.setMeterOfRange(rangeBetweenLines);
                        }

                        Intent strMaps = new Intent(context, MapsActivity.class);
                        strMaps.putParcelableArrayListExtra("Field", mPointsForField);
                        strMaps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(strMaps);
                        }
                    });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String childNameHoldClick = (String) adapterView.getItemAtPosition(i);
                        dataSnapshot.child(childNameHoldClick).getRef().removeValue();

                        //These two rows is to refresh when item removed
                        adapter.clear();
                        adapter.notifyDataSetChanged();

                        return true;
                    }
                });
                //TODO: Finnish SetEmptyView
//                listView.setEmptyView();
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