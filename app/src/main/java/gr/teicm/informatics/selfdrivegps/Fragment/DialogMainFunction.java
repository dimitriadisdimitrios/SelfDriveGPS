package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                DialogUtilities.chooseWhichDialogWillAppear(0, 4, mView); //Set through function visibility

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_field)
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    pointsForField.clear(); //Empty ArrayList<LatLng> from the controller
                                    controller.setArrayListForField(pointsForField); //Set the cleared arrayList to Controller.java
                                    Toast.makeText(getContext(), "Preparation for sending Canceled, try again!", Toast.LENGTH_SHORT).show();
                                    controller.getGoogleMap().clear(); // Clear the map to re-draw the polyLines
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
                        Boolean isNameBeenAccepted = false; //Is a measure to control when the name for FireBase is acceptable

                        EditText collectionOfLatLng = mView.findViewById(R.id.et_pop_name_DB_ET); //Set Button from layout_pop
                        String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox

//                        DialogUtilities.checkNameIfExistInBase(nameOfDataBaseKey);

                        if (!nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mAuth.getUid()!=null) {
                            //Create child with specific name which include LatLng for field
                            Log.d(TAG, String.valueOf(controller.getIfFoundMatchOnFireBase()));
                            //TODO: Under constructed
//                            if(!controller.getIfFoundMatchOnFireBase()){
                                databaseReference.child("users/" + mAuth.getUid() + "/" + nameOfDataBaseKey + "/Polygon").setValue(pointsForField);
                                Toast.makeText(getContext(), "LatLng for Field: Have been added", Toast.LENGTH_SHORT).show();
                                controller.setIdOfListView(nameOfDataBaseKey);
                                controller.setProgramStatus(Controller.MODE_2_CREATE_LINE);
                                MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); //Re-draw the map with necessary resources
                                isNameBeenAccepted = true;
//                            }else{
//                                controller.setIfFoundMatchOnFireBase(false);
//                                Toast.makeText(getContext(), "This name already used. Try Again!", Toast.LENGTH_SHORT).show();
//                            }
                        } else if (nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Toast.makeText(getContext(), "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                        }
                        if(isNameBeenAccepted){
                            mDialog.dismiss();
                        }
                    }
                });
            break;

            case Controller.MODE_2_CREATE_LINE:
                Log.d(TAG, "Create route line");
                DialogUtilities.chooseWhichDialogWillAppear(4, 0, mView); //Set through function visibility
                DialogUtilities.enableRangeMeter(mView, getActivity().getApplication().getBaseContext()); //Call the range meter for dialog

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_line)
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mAuth.getUid()!=null) {
                                    //Add ArrayList for PolyLine on same child
                                    databaseReference.child(mAuth.getUid()).child(controller.getIdOfListView()).child("Polyline").setValue(pointsForLine);
                                    databaseReference.child(mAuth.getUid()).child(controller.getIdOfListView()).child("Meter").setValue(controller.getMeterOfRange());
                                    controller.setProgramStatus(Controller.MODE_3_DRIVING);
                                    MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); //Re-draw the map with necessary resources

                                    Toast.makeText(getContext(), "LatLng for Polyline: Have been added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    pointsForLine.clear(); //Empty ArrayList<LatLng> from the controller
                                    controller.setArrayListForLine(pointsForLine);
                                    controller.getGoogleMap().clear(); // Clear the map to re-draw the polyLines
                                    MapsUtilities.placePolygonForRoute(controller.getArrayListForField(), controller.getGoogleMap());

                                    Toast.makeText(getContext(), "Preparation for line Canceled !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                mDialog = builder.create(); // Create the AlertDialog object and return it
            break;

            case Controller.MODE_3_DRIVING:
                Log.d(TAG, "Driving mode");
                DialogUtilities.chooseWhichDialogWillAppear(4, 0, mView); //Set through function visibility
                DialogUtilities.enableRangeMeter(mView, getActivity().getApplication().getBaseContext()); //Call the range meter for dialog

                TextView tvRangeMeter = mView.findViewById(R.id.tv_range_of_field_meter);
                tvRangeMeter.setText((""+controller.getMeterOfRange())); // Depict the range of Lines which have

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_driving)
                        .setPositiveButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mAuth.getUid()!=null) {
                                    //Add ArrayList for PolyLine on same child
                                    databaseReference.child(mAuth.getUid()).child(controller.getIdOfListView()).child("Meter").setValue(controller.getMeterOfRange());
                                    Toast.makeText(getContext(), "Range between lines, changed", Toast.LENGTH_SHORT).show();
                                    MapsUtilities.recreateFieldWithMultiPolyline(controller.getGoogleMap()); //Re-draw the map with necessary resources
                                }
                            }
                        });
                mDialog = builder.create(); // Create the AlertDialog object and return it
                break;
        }
        return mDialog;
    }
}