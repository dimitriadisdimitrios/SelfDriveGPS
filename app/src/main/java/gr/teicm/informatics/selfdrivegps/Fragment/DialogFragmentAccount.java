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

import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.R;

public class DialogFragmentAccount extends android.app.DialogFragment{
    private FirebaseAuth mAuth;
    private final String TAG = "DialogAccount";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.show_account_layout, nullParent);

        Button btnLogOut = mView.findViewById(R.id.btn_log_out);
        mAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                dismiss();
                Log.d(TAG, "You successfully logged out");
            }
        });

        builder.setView(mView)
                .setMessage("Account").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }
}
