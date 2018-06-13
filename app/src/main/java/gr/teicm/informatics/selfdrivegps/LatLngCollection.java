package gr.teicm.informatics.selfdrivegps;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Dimitriadis983 on 13-Mar-18.
 */

public class LatLngCollection extends AsyncTask<String, Void, String> {
    private String TAG = "LatLngCollection";
    private ArrayList<LatLng> points = new ArrayList<>();
    //TODO: Fix the warning

    private Context context;

    public LatLngCollection(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        final String childName = strings[0];
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(childName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, String.valueOf(dataSnapshot.getChildrenCount()));
//                int childrenCount = (int) dataSnapshot.getChildrenCount();
//                for(int i=0; i<childrenCount; i++){
//
//                }
                for (DataSnapshot snapm: dataSnapshot.getChildren()) {
                    Double latitude = snapm.child("latitude").getValue(Double.class);
                    Double longitude = snapm.child("longitude").getValue(Double.class);
                    LatLng latLng = new LatLng(latitude, longitude);
                    points.add(latLng);
                    Log.d(TAG, latitude+"\t"+longitude+"\n");
                }


                //TODO: Check warning!!!
                //Save value of LatLng of chosen name of ListView
//                ArrayList<LatLng> listOfLatLng = (ArrayList<LatLng>) dataSnapshot.getValue();

                Intent strMaps = new Intent(context, MapsActivity.class);
                strMaps.putExtra("buttonStatus", "invisible");
                strMaps.putParcelableArrayListExtra("latLng", points);
                context.startActivity(strMaps);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return childName;
    }

    @Override
    protected void onPostExecute(String arrayList) {
        super.onPostExecute(arrayList);
        Toast.makeText(context, arrayList, Toast.LENGTH_LONG).show();
    }
}
