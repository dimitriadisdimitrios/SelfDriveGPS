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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.R;


public class DialogFragmentUtility extends DialogFragment {
    private Controller controller = new Controller();
    private final static String TAG = "DialogFragmentUtility";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<LatLng> pointsForField = controller.getArrayListForField();
        final ArrayList<LatLng> pointsForLine = controller.getArrayListForLine();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mView = inflater.inflate(R.layout.activity_pop,null);

        EditText editTextToSaveNameOfField = mView.findViewById(R.id.et_pop_name_DB_ET);
        LinearLayout linearLayoutIncludeRangeMeter = mView.findViewById(R.id.linear_layout_with_range_meter);

        switch (controller.getProgramStatus()) {
            case "Record Field":

                Log.d(TAG, "Record field selected");
                editTextToSaveNameOfField.setVisibility(View.VISIBLE);
                linearLayoutIncludeRangeMeter.setVisibility(View.INVISIBLE);

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_field)
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Toast.makeText(getContext(), "Preparation for sending Canceled !", Toast.LENGTH_SHORT).show();
                                    pointsForField.clear(); //Empty ArrayList<LatLng> from the controller
                                }
                            }
                        })
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText collectionOfLatLng = mView.findViewById(R.id.et_pop_name_DB_ET); //Set Button from layout_pop
                                String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //Connect FireBase Database so I will able to use it

                                if (!nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //TODO: Disconnect function to save name for database
//                                    databaseReference.child(nameOfDataBaseKey).setValue(pointsForField); //Create child with specific name which include LatLng
                                    Toast.makeText(getContext(), "LatLng have been added", Toast.LENGTH_SHORT).show();
                                    controller.setProgramStatus(Controller.MODE_1_CREAT_LINE);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Toast.makeText(getContext(), "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                break;

            case "Create Line":

                Log.d(TAG, "Create route line");
                editTextToSaveNameOfField.setVisibility(View.INVISIBLE);
                linearLayoutIncludeRangeMeter.setVisibility(View.VISIBLE);

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_line)
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Toast.makeText(getContext(), "Preparation for line Canceled !", Toast.LENGTH_SHORT).show();
                                    pointsForLine.clear(); //Empty ArrayList<LatLng> from the controller
                                }
                            }
                        })
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Toast.makeText(getContext(), "Preparation for line YES!!! !", Toast.LENGTH_SHORT).show();
                                    controller.setProgramStatus(Controller.MODE_2_DRIVING);
                                }

                            }
                        });
                break;

        }
        return builder.create(); // Create the AlertDialog object and return it
    }

}
