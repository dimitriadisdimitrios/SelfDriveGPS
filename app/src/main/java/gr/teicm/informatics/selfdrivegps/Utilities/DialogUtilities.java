package gr.teicm.informatics.selfdrivegps.Utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;

public class DialogUtilities {
    private static Controller controller = new Controller();
    private static final String TAG = "DialogUtilies";


    private static void counterForRangeOfField(String function, TextView tvRangeOfLines, Context context){
        int counter = Integer.parseInt(tvRangeOfLines.getText().toString());

        if(function.equals("plus")) {
            if(counter < 20){
                counter = counter + 1; //Increase the meter
            }
        }else if(function.equals("sub")){
            if(counter > 5) {
                counter = counter - 1; //Decrease the meter
            }
        }
        controller.setMeterOfRange(counter); //Set counter to Controller
        tvRangeOfLines.setText(context.getString(R.string.tv_meter_of_range_for_field,counter)); //Show counter to textView as result
    }

    public static void chooseTerrainBasedOfRadioBtn(String mName){
        switch (mName) {
            case "Normal":
                controller.getGoogleMap().setMapType(1); //To make it normal
                Log.d(TAG, "Something happer 1");
                break;
            case "Satellite":
                controller.getGoogleMap().setMapType(2); //To make it satellite
                Log.d(TAG, "Something happer 2");
                break;
            case "Terrain":
                controller.getGoogleMap().setMapType(3); //To make it terrain
                Log.d(TAG, "Something happer 3");
                break;
                default:
                    Log.d(TAG, "Something happer !!!");
                    break;
        }
    }

    public static void chooseWhichDialogWillAppear(int etFieldName, int llRangeMeter, View mView){
        EditText editTextToSaveNameOfField = mView.findViewById(R.id.et_pop_name_DB_ET);
        LinearLayout linearLayoutIncludeRangeMeter = mView.findViewById(R.id.linear_layout_with_range_meter);

        // Visible = 0 || Invisible = 4
        editTextToSaveNameOfField.setVisibility(etFieldName);
        linearLayoutIncludeRangeMeter.setVisibility(llRangeMeter);
    }

    public static void enableRangeMeter(View mView, final Context context){

        Button btPlus = mView.findViewById(R.id.btn_plus);
        Button btSub = mView.findViewById(R.id.btn_sub);
        final TextView tvRangeBetweenLines = mView.findViewById(R.id.tv_range_of_field_meter);

        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtilities.counterForRangeOfField("plus", tvRangeBetweenLines, context);
            }
        });
        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtilities.counterForRangeOfField("sub", tvRangeBetweenLines, context);
            }
        });
    }
}
