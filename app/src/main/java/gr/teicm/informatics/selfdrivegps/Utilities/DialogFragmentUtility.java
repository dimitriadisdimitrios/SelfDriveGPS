package gr.teicm.informatics.selfdrivegps.Utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.teicm.informatics.selfdrivegps.R;


public class DialogFragmentUtility extends DialogFragment {
    private MapsUtilities mapsUtilities = new MapsUtilities();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: Find a way to remove this IF statement
        String TAG = "DialogFragmentUtility";
        Log.d(TAG, String.valueOf(mapsUtilities.getPoints()));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mView = inflater.inflate(R.layout.activity_pop,null);
        builder.setView(mView);
        builder.setMessage(R.string.pop_to_send_latLng)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Toast.makeText(getContext(),"Preparation for sending Canceled !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText collectionOfLatLng =  mView.findViewById(R.id.pop_name_DB_ET); //Set Button from layout_pop
                        String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //Connect FireBase Database so I will able to use it
                        if(!nameOfDataBaseKey.matches("")) {
                            databaseReference.child(nameOfDataBaseKey).setValue(mapsUtilities.getPoints()); //Create child with specific name which include LatLng
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Toast.makeText(getContext(), "LatLng have been added", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Toast.makeText(getContext(), "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        return builder.create(); // Create the AlertDialog object and return it
    }
}