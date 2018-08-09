package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Utilities.Controller;
import gr.teicm.informatics.selfdrivegps.Utilities.MapsUtilities;


public class DialogFragment extends android.app.DialogFragment {
    private Controller controller = new Controller();
    private final static String TAG = "DialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<LatLng> pointsForField = controller.getArrayListForField();
        final ArrayList<LatLng> pointsForLine = controller.getArrayListForLine();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final ViewGroup nullParent = null;
        final View mView = inflater.inflate(R.layout.activity_pop, nullParent);

        EditText editTextToSaveNameOfField = mView.findViewById(R.id.et_pop_name_DB_ET);
        LinearLayout linearLayoutIncludeRangeMeter = mView.findViewById(R.id.linear_layout_with_range_meter);
        LinearLayout linearLayoutForTerrainChange = mView.findViewById(R.id.linear_layout_for_change_terrain);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(); //Connect FireBase Database so I will able to use it

        switch (controller.getProgramStatus()) {
            case Controller.MODE_1_RECORD_FIELD:
                controller.setProgramStatus(Controller.MODE_2_CREATE_LINE);

                Log.d(TAG, "Record field selected");
                editTextToSaveNameOfField.setVisibility(View.VISIBLE);
                linearLayoutIncludeRangeMeter.setVisibility(View.INVISIBLE);
                linearLayoutForTerrainChange.setVisibility(View.INVISIBLE);

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_field)
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Toast.makeText(getContext(), "Preparation for sending Canceled !", Toast.LENGTH_SHORT).show();
                                    controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD);
                                    pointsForField.clear(); //Empty ArrayList<LatLng> from the controller
                                }
                            }
                        })
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText collectionOfLatLng = mView.findViewById(R.id.et_pop_name_DB_ET); //Set Button from layout_pop
                                String nameOfDataBaseKey = collectionOfLatLng.getText().toString(); //Get text from editBox

                                if (!nameOfDataBaseKey.matches("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //Create child with specific name which include LatLng for field
                                    databaseReference.child(nameOfDataBaseKey).child("Polygon").setValue(pointsForField);
                                    Toast.makeText(getContext(), "LatLng for Field: Have been added", Toast.LENGTH_SHORT).show();
                                    controller.setIdOfListView(nameOfDataBaseKey);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Toast.makeText(getContext(), "Name of Key is empty !", Toast.LENGTH_SHORT).show();
                                        controller.setProgramStatus(Controller.MODE_1_RECORD_FIELD);
                                        dialog.cancel();
                                    }
                                }
                            }
                        });
                break;

            case Controller.MODE_2_CREATE_LINE:
                controller.setProgramStatus(Controller.MODE_3_DRIVING);

                Log.d(TAG, "Create route line");
                editTextToSaveNameOfField.setVisibility(View.INVISIBLE);
                linearLayoutIncludeRangeMeter.setVisibility(View.VISIBLE);
                linearLayoutForTerrainChange.setVisibility(View.INVISIBLE);


                final Button btPlus = mView.findViewById(R.id.btn_plus);
                final Button btSub = mView.findViewById(R.id.btn_sub);
                final TextView tvRangeBetweenLines = mView.findViewById(R.id.tv_range_of_field_meter);

                btPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapsUtilities.counterForRangeOfField("plus", tvRangeBetweenLines, getActivity().getApplication().getBaseContext());
                    }
                });
                btSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapsUtilities.counterForRangeOfField("sub", tvRangeBetweenLines, getActivity().getApplication().getBaseContext());
                    }
                });

                builder.setView(mView)
                        .setMessage(R.string.label_on_dialog_create_line)
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //Add ArrayList for PolyLine on same child
                                    databaseReference.child(controller.getIdOfListView()).child("Polyline").setValue(pointsForLine);
                                    databaseReference.child(controller.getIdOfListView()).child("Meter").setValue(controller.getMeterOfRange());
                                    Toast.makeText(getContext(), "LatLng for Polyline: Have been added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton(R.string.bt_on_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Toast.makeText(getContext(), "Preparation for line Canceled !", Toast.LENGTH_SHORT).show();
                                    controller.setProgramStatus(Controller.MODE_2_CREATE_LINE);
                                    pointsForLine.clear(); //Empty ArrayList<LatLng> from the controller
                                }
                            }
                        });
                break;
            case Controller.MODE_0_SET_TERRAIN:
                controller.setProgramStatus(controller.getProgramLastStatus());

                Log.d(TAG, "Terrain changing");
                editTextToSaveNameOfField.setVisibility(View.INVISIBLE);
                linearLayoutIncludeRangeMeter.setVisibility(View.INVISIBLE);
                linearLayoutForTerrainChange.setVisibility(View.VISIBLE);

                final RadioGroup radioGroupAboutTerrain = mView.findViewById(R.id.rg_terrain_change);

                builder.setView(mView)
                        .setMessage("Change terrain of map")
                        .setNegativeButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    int temp = radioGroupAboutTerrain.getCheckedRadioButtonId();
                                    Log.d(TAG, String.valueOf(temp));
                                    Toast.makeText(getContext(), "LatLng for Polyline: Have been added"+temp, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
        return builder.create(); // Create the AlertDialog object and return it
    }
}
