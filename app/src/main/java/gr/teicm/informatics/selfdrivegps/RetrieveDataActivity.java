package gr.teicm.informatics.selfdrivegps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class RetrieveDataActivity extends AppCompatActivity {
    final String TAG = "RetrieveDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference demoRef = rootRef.child("Demo");
//        demoRef.push().setValue("asd123");

        //TODO: Find a way to retrieve Data from a specific child
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                System.out.println("!!!!! Testing fireBase !!!" + map +"   !!!!!");
                Log.d(TAG, "!!!!! Testing fireBase !!! " + map +"   !!!!!");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

//        HashMap<LatLng, String> retrieveLatLngData = new HashMap<>();
    }
}