package gr.teicm.informatics.selfdrivegps;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class RetrieveDataActivity extends Activity {
    final String TAG = "RetrieveDataActivity";
    public static final int REQUEST_CODE_SECOND_ACTIVITY = 100; // This value can be any number. It doesn't matter
    public static final String SHOW_BUTTON = "shouldShowButton"; //at all. The only important thing is to have the
                                                                //same value you started the child activity with when you're checking the onActivityResult.
    //TODO: Create a list view with name of keys
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);

        //TODO: Revision layout page
        Button startMapsWithDataBtn = (Button) findViewById(R.id.send_data_to_mapsActivity_btn);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference demoRef = rootRef.child("Demo");
//        demoRef.push().setValue("asd123");
        startMapsWithDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent strMaps = new Intent(RetrieveDataActivity.this, MapsActivity.class);
                RetrieveDataActivity.this.startActivity(strMaps);
            }
        });

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
    }

//  https://stackoverflow.com/questions/34951270/how-to-set-a-button-visible-from-another-activity-in-android/34951687
    //TODO: Hide btn when i transfer on Maps Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY && resultCode == RESULT_OK) {
            //Check if you passed 'true' from the other activity to show the button, and also, only set visibility to VISIBLE if the view is not yet VISIBLE
//            if (data.hasExtra(SHOW_BUTTON) && data.getBooleanExtra(SHOW_BUTTON, false) && mMyButtonToBeHidden.getVisibility() != View.VISIBLE) {
//                mMyButtonToBeHidden.setVisibility(View.VISIBLE);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        //Back Btn do nothing !
//        super.onBackPressed();
    }
}