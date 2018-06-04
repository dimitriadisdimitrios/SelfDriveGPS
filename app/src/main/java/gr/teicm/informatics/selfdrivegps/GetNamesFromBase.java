package gr.teicm.informatics.selfdrivegps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetNamesFromBase extends AsyncTask<Void,Void,String[]> {

    private String TAG = "GetNamesFromBase";
    private String[] fList = new String[10];
    private int counter = 0;

    public void helloButtonWasPressed() {
        if (fList != null) {
            Log.d(TAG, "hi there, " + fList);
        }}

    private void getNames(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    fList[counter] = id;
//                    Log.d(TAG,fList[counter]);
                    counter++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



    @Override
    protected String[] doInBackground(Void... voids) {
        getNames();
        helloButtonWasPressed();
        for(int i=0;i<fList.length; i++){
            Log.d(TAG,"!!"+fList[i]);
        }

        return fList;
    }

    @Override
    protected void onPostExecute(String s[]) {
//        for(int i=0;i<s.length; i++){
//            Log.d(TAG,"!!"+s[0]);
//        }

    }
}
