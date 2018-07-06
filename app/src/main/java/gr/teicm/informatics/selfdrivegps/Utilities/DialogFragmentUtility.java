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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.R;


public class DialogFragmentUtility extends DialogFragment {
    private Controller controller = new Controller();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: Find a way to remove this IF statement
        String TAG = "DialogFragmentUtility";
        Log.d(TAG, String.valueOf(controller.getPoints()));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mView = inflater.inflate(R.layout.activity_pop,null);
        builder.setView(mView);
        builder.setMessage(R.string.label_on_dialog)
                .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Toast.makeText(getContext(),"Preparation for sending Canceled !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText collectionOfLatLng =  mView.findViewById(R.id.pop_name_DB_ET); //Set Button from layout_pop
                        String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //Connect FireBase Database so I will able to use it
                        if(!nameOfDataBaseKey.matches("")) {
                            ArrayList<LatLng> mPoints = controller.getPoints();
                            databaseReference.child(nameOfDataBaseKey).setValue(mPoints); //Create child with specific name which include LatLng
                            Log.d("dialog", String.valueOf(controller.getPoints()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Toast.makeText(getContext(), "LatLng have been added", Toast.LENGTH_SHORT).show();
                                mPoints.clear();
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