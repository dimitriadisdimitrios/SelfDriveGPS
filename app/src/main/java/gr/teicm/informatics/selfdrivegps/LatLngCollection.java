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
import java.util.List;

/**
 * Created by Dimitriadis983 on 13-Mar-18.
 */

public class LatLngCollection extends AsyncTask<String, Void, String> {
    private String TAG = "LatLngCollection";
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
                //TODO: Check warning!!!
                //Save value of LatLng of chosen name of ListView
                ArrayList<LatLng> listOfLatLng = (ArrayList<LatLng>) dataSnapshot.getValue();
                if(listOfLatLng == null) {
                    Log.d(TAG, "Attempt to invoke interface method 'java.util.Collection java.util.Map.values()' on a null object reference");
                }
                Intent strMaps = new Intent(context, MapsActivity.class);
                strMaps.putExtra("buttonStatus", "invisible");
                strMaps.putParcelableArrayListExtra("latLng", listOfLatLng);
                context.startActivity(strMaps);
                for(int i=0;i<listOfLatLng.size();i++) {
//                    Log.d(TAG, String.valueOf(listOfLatLng.get(i)));
                }
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
