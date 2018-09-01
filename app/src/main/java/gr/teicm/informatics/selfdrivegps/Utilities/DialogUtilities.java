package gr.teicm.informatics.selfdrivegps.Utilities;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;

public class DialogUtilities {
    private static Controller controller = new Controller();
//    private static final String TAG = "DialogUtilities";

    public static void chooseTerrainBasedOfRadioBtn(String mName){
        switch (mName) {
            case "Normal":
                controller.getGoogleMap().setMapType(1); //To make it normal
                break;
            case "Satellite":
                controller.getGoogleMap().setMapType(2); //To make it satellite
                break;
            case "Terrain":
                controller.getGoogleMap().setMapType(3); //To make it terrain
                break;
        }
    }

    public static void chooseWhichDialogWillAppear(int etFieldName, int tvSaveLine, View mView){
        EditText editTextToSaveNameOfField = mView.findViewById(R.id.et_pop_name_DB_ET);
        TextView textViewToSaveLine = mView.findViewById(R.id.tv_line_saving);

        // Visible = 0 || Invisible = 4
        editTextToSaveNameOfField.setVisibility(etFieldName);
        textViewToSaveLine.setVisibility(tvSaveLine);
    }
}
