package gr.teicm.informatics.selfdrivegps.Fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import gr.teicm.informatics.selfdrivegps.Utilities.DialogUtilities;
import gr.teicm.informatics.selfdrivegps.Utilities.MapsUtilities;

public class DialogMainFunction extends android.app.DialogFragment {
    private Controller controller = new Controller();
    private final static String TAG = "DialogMainFunction";
    private AlertDialog mDialog;
    private FirebaseAuth mAuth;

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<LatLng> pointsForField = controller.getArrayListForField();
        final ArrayList<LatLng> pointsForLine = controller.getArrayListForLine();

        // Use the Builder class for convenient mDialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.dialog_main_function, nullParent); // Inflate the layout to interact with xml

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //Connect FireBase Database so I will able to use it
        mAuth = FirebaseAuth.getInstance();

        switch (controller.getProgramStatus()) {
            case Controller.MODE_1_RECORD_FIELD:
                Log.d(TAG, "Record field selected");
                DialogUtilities.chooseWhichDialogWillAppear(0, 8, mView); //Set through function visibility

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_field)
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    pointsForField.clear(); //Empty ArrayList<LatLng> from the controller
                                    controller.setArrayListForField(pointsForField); //Set the cleared arrayList to Controller.java
                                    Toast.makeText(getContext(), "Preparation for sending Canceled, try again!", Toast.LENGTH_SHORT).show();
                                    MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); // Clear the map to re-draw the polyLines
                                }
                            }
                        })
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Nothing here to override next
                            }
                        });
                mDialog = builder.create(); // Create the AlertDialog object and return it

                mDialog.show(); //Override Negative btn to control the editText
                mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText collectionOfLatLng = mView.findViewById(R.id.et_pop_name_DB_ET); //Set Button from layout_pop
                        final String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox

                        if (!nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mAuth.getUid() != null) {
                            //Create child with specific name which include LatLng for field

                            //Check if name that user gave to field
                            databaseReference.child("users/" + mAuth.getUid() + "/" + nameOfDataBaseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Field "name" already exists
                                        // Let the user know he needs to pick another username.
                                        Toast.makeText(getContext(), "Error: Field name already used", Toast.LENGTH_SHORT).show();

                                    } else {
                                        databaseReference.child("users/" + mAuth.getUid() + "/" + nameOfDataBaseKey + "/Polygon").setValue(pointsForField);
                                        Toast.makeText(getContext(), "LatLng for Field: Have been added", Toast.LENGTH_SHORT).show();
                                        controller.setIdOfListView(nameOfDataBaseKey);
                                        controller.setProgramStatus(Controller.MODE_2_CREATE_LINE);
                                        MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); //Re-draw the map with necessary resources
                                        mDialog.dismiss(); //If is name has not any problem . Dismiss the dialog
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        } else if (nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Toast.makeText(getContext(), "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case Controller.MODE_2_CREATE_LINE:
                Log.d(TAG, "Create route line");
                DialogUtilities.chooseWhichDialogWillAppear(8, 0, mView); //Set through function visibility

                builder.setView(mView)
                        .setMessage("Are you sure?")
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mAuth.getUid() != null) {
                                    //Add ArrayList for PolyLine on same child "users/" + mAuth.getUid() + "/" + nameOfDataBaseKey + "/
                                    databaseReference.child("users/" + mAuth.getUid() + "/" + controller.getIdOfListView() + "/Polyline").setValue(pointsForLine);
                                    controller.setProgramStatus(Controller.MODE_3_DRIVING);
                                    MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); //Re-draw the map with necessary resources

                                    Toast.makeText(getContext(), "LatLng for Polyline: Have been added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    controller.getArrayListForLine().clear(); //Empty ArrayList<LatLng> from the controller
                                    controller.getMarkerPosition().clear();
                                    MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap());
                                    Toast.makeText(getContext(), "Preparation for line Canceled !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                mDialog = builder.create(); // Create the AlertDialog object and return it
                break;
        }
        return mDialog;
    }
}