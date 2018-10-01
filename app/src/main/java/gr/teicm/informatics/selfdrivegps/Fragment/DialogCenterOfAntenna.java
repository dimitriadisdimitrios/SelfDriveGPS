package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import gr.teicm.informatics.selfdrivegps.R;
import gr.teicm.informatics.selfdrivegps.Utilities.DialogUtilities;

public class DialogCenterOfAntenna extends android.app.DialogFragment{

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

        builder.setView(mView)
                .setMessage("Choose antenna distance")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Save the changed settings
                        DialogUtilities.setUpAntennaNewPosition(true, tvFront, tvBack, tvLeft, tvRight, getActivity());
                    }
                })
                .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Set all sides to 0
                        DialogUtilities.setUpAntennaNewPosition(false, tvFront, tvBack, tvLeft, tvRight, getActivity());
                    }
                });

        return builder.create();
    }

}
