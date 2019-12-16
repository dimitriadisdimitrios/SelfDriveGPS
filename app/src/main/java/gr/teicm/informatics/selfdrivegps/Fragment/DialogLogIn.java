package gr.teicm.informatics.selfdrivegps.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;

public class DialogLogIn extends android.app.DialogFragment {
    private final static String TAG = "DialogLogIn";

    private EditText etEmailToLogIn, etPasswordToLogIn;

    private Controller controller = new Controller();
    private FirebaseAuth mAuth;
    private AlertDialog mDialog;

    @Override
    public AlertDialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View mView = inflater.inflate(R.layout.dialog_log_in, null);

        etEmailToLogIn = mView.findViewById(R.id.et_email_log_in);
        etPasswordToLogIn = mView.findViewById(R.id.et_password_log_in);
        mAuth = FirebaseAuth.getInstance();

        TextView tvCreateAccount = mView.findViewById(R.id.tv_create_account);

        //Open dialog to create new account !
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCreateAccount dialogCreateAccount = new DialogCreateAccount();
                dialogCreateAccount.show(controller.getAppFragmentManager(), "Create Account");
                dialogCreateAccount.setCancelable(false);
                mDialog.dismiss();
            }
        });

        //Dialog to Log In to an account
        builder.setView(mView)
                .setMessage("Log in")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        mAuth.signOut();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
        mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = etEmailToLogIn.getText().toString();
                String mPassword = etPasswordToLogIn.getText().toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {
                        Toast.makeText(getContext(), "Fill is empty !", Toast.LENGTH_SHORT).show();
                    }else if (!Patterns.EMAIL_ADDRESS.matcher(etEmailToLogIn.getText().toString()).matches()) {
                        //To be sure that email is in right Form
                        Toast.makeText(getContext(), "Email form is wrong !", Toast.LENGTH_SHORT).show();
                    }else{
                        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "You're email/password isn't correct", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "You are logged in", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        });
        return mDialog;
    }

    //Start to sign in. If you signed in then you must sign out because account stay in app
    private void startSignIn(){
        String mEmail = etEmailToLogIn.getText().toString();
        String mPassword = etPasswordToLogIn.getText().toString();

        if(TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)){
            Log.d(TAG, "Fill is empty !");
        }else{
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Log.d(TAG, "You're email/password isn't correct");
                    }
                }
            });
        }
    }
}
