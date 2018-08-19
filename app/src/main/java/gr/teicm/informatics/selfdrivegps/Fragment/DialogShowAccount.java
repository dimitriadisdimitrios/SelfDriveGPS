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

import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.R;

public class DialogShowAccount extends android.app.DialogFragment{
    private FirebaseAuth mAuth;
    private final String TAG = "DialogAccount";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.dialog_show_account, nullParent);

        Button btnLogOut = mView.findViewById(R.id.btn_log_out);
        TextView tvUserMail = mView.findViewById(R.id.tv_logged_in_username_show);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            tvUserMail.setText(mAuth.getCurrentUser().getEmail());
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                dismiss();
                Log.d(TAG, "You successfully logged out");
            }
        });

        builder.setView(mView)
                .setMessage("You logged in, as:").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }
}
