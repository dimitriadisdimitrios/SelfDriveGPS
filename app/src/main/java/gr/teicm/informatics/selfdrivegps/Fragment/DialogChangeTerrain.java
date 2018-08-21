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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Utilities.DialogUtilities;

public class DialogChangeTerrain extends  android.app.DialogFragment {
    private static final String TAG = "DialogChangeTerrain";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "Terrain changing");
        // Use the Builder class for convenient mDialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.dialog_change_terrain, nullParent); // Inflate the layout to interact with xml

        builder.setView(mView)
                .setMessage("Change terrain of map")
                .setPositiveButton(R.string.bt_on_dialog_send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            RadioGroup radioGroupAboutTerrain = mView.findViewById(R.id.rg_terrain_change); //Reference to RadioGroup that need to focus
                            int idOfCheckedRadioButton = radioGroupAboutTerrain.getCheckedRadioButtonId(); //Get id of radio button is checked
                            RadioButton radioButtonWithTerrain =  mView.findViewById(idOfCheckedRadioButton); //Find the radioButton on layout

                            DialogUtilities.chooseTerrainBasedOfRadioBtn(radioButtonWithTerrain.getText().toString());
                            Toast.makeText(getContext(), radioButtonWithTerrain.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return builder.create(); // Create the AlertDialog object and return it
    }
}
