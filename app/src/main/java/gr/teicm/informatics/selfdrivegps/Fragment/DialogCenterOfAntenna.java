package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Utilities.DialogUtilities;

public class DialogCenterOfAntenna extends android.app.DialogFragment{
    Controller controller = new Controller();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.dialog_change_antenna_position, nullParent);

        Button btFrontPlus = mView.findViewById(R.id.btn_antenna_front_plus);
        Button btFrontSub = mView.findViewById(R.id.btn_antenna_front_sub);
        Button btBackPlus = mView.findViewById(R.id.btn_antenna_back_plus);
        Button btBackSub = mView.findViewById(R.id.btn_antenna_back_sub);
        Button btLeftPlus = mView.findViewById(R.id.btn_antenna_left_plus);
        Button btLeftSub = mView.findViewById(R.id.btn_antenna_left_sub);
        Button btRightPlus = mView.findViewById(R.id.btn_antenna_right_plus);
        Button btRightSub = mView.findViewById(R.id.btn_antenna_right_sub);
        final TextView tvFront = mView.findViewById(R.id.tv_antenna_front);
        final TextView tvBack = mView.findViewById(R.id.tv_antenna_back);
        final TextView tvRight = mView.findViewById(R.id.tv_antenna_right);
        final TextView tvLeft = mView.findViewById(R.id.tv_antenna_left);

        DialogUtilities.setListenerForClickBtn(btFrontPlus, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btFrontSub, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btBackPlus, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btBackSub, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btRightPlus, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btRightSub, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btLeftPlus, tvFront, tvBack, tvRight, tvLeft);
        DialogUtilities.setListenerForClickBtn(btLeftSub, tvFront, tvBack, tvRight, tvLeft);

        if(controller.getSharePreferences().contains("front")){ //Set the values as previously had been set
            tvFront.setText(String.valueOf(controller.getSharePreferences().getInt("front", 8)));
            tvBack.setText(String.valueOf(controller.getSharePreferences().getInt("back", 8)));
            tvLeft.setText(String.valueOf(controller.getSharePreferences().getInt("left", 8)));
            tvRight.setText(String.valueOf(controller.getSharePreferences().getInt("right", 8)));
        }

        builder.setView(mView)
                .setMessage("Choose antenna distance")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Save the changed settings
                        DialogUtilities.setUpAntennaNewPosition(true, tvFront, tvBack, tvLeft, tvRight);
                    }
                })
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Set all sides to 0
                        DialogUtilities.setUpAntennaNewPosition(false, tvFront, tvBack, tvLeft, tvRight);
                    }
                });

        return builder.create();
    }

}
