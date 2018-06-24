package gr.teicm.informatics.selfdrivegps;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LatLngCollection extends AsyncTask<String, Void, String> {
//    private String TAG = "LatLngCollection";
    private ArrayList<LatLng> points = new ArrayList<>();
    //TODO: Fix the warning

    public Context context;

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
                for (DataSnapshot childCount: dataSnapshot.getChildren()) {
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
