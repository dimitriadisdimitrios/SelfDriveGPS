package gr.teicm.informatics.selfdrivegps.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Secured. Use Uid to get data
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users/" + mAuth.getUid());
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
                    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                        String childName = (String) adapterView.getItemAtPosition(i);
                        controller.setIdOfListView(childName); //In case that user want to delete an item

                        //Secure that if user press backPress he will take the right information
                        mPointsForField.clear();
                        mPointsForLine.clear();

                        //Get the main frame of field
                        for (DataSnapshot childCount: dataSnapshot.child(childName).child("Polygon").getChildren()) {
                            Double latitude = childCount.child("latitude").getValue(Double.class);
                            Double longitude = childCount.child("longitude").getValue(Double.class);
                            if(latitude!=null && longitude!=null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                mPointsForField.add(latLng);
                                controller.setArrayListForField(mPointsForField);
                            }
                        }
                        //Get the main line from DB
                        for (DataSnapshot childCount: dataSnapshot.child(childName).child("Polyline").getChildren()) {
                            Double latitude = childCount.child("latitude").getValue(Double.class);
                            Double longitude = childCount.child("longitude").getValue(Double.class);
                            if(latitude!=null && longitude!=null) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                mPointsForLine.add(latLng);
                                controller.setArrayListForLine(mPointsForLine);
                            }
                        }
                        //Get rangeMeters for lines from DB
                        Integer rangeBetweenPolyLines = dataSnapshot.child(childName).child("Meter").getValue(Integer.class);
                        if (rangeBetweenPolyLines != null){
                            int rangeBetweenLines = rangeBetweenPolyLines;
                            controller.setMeterOfRange(rangeBetweenLines);
                            Log.d(TAG, String.valueOf(controller.getMeterOfRange()));
                        }

                        //TODO: work on here to be sure tha information you need is completed
                        if(mPointsForLine != null && mPointsForField != null && rangeBetweenPolyLines != null){
                            Intent strMaps = new Intent(context, MapsActivity.class);
                            strMaps.putParcelableArrayListExtra("Field", mPointsForField);
                            strMaps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(strMaps);
                        }else{
                            //Alert Dialog which ask confirmation before delete a field with not sufficient data
                            AlertDialog.Builder adb =new AlertDialog.Builder(RetrieveDataActivity.this);
                            adb.setTitle("Error !");
                            adb.setMessage("While recording something went wrong and your field is going to be deleted");
                            adb.setNegativeButton("Ok", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String childNameHoldClick = (String) adapterView.getItemAtPosition(i);
                                    dataSnapshot.child(childNameHoldClick).getRef().removeValue();
                                    adapter.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            adb.show();
                        }
                    }
                    });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                        String childName = (String) adapterView.getItemAtPosition(i);

                        //Alert Dialog which ask confirmation before delete a field
                        AlertDialog.Builder adb =new AlertDialog.Builder(RetrieveDataActivity.this);
                        adb.setTitle("Delete ?");
                        adb.setMessage("Are you sure you want to delete field  \"" + childName + "\" ?");
                        adb.setPositiveButton(R.string.bt_on_dialog_no, null);
                        adb.setNegativeButton(R.string.bt_on_dialog_yes, new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String childNameHoldClick = (String) adapterView.getItemAtPosition(i);
                                dataSnapshot.child(childNameHoldClick).getRef().removeValue();
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        adb.show();

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
        startActivity(new Intent(RetrieveDataActivity.this, MainActivity.class));
//        super.onBackPressed();
    }
}