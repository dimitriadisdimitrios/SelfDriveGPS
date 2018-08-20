package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.R;

public class DialogLogIn extends android.app.DialogFragment {
    private final static String TAG = "DialogLogIn";

    private EditText etEmailToLogIn, etPasswordToLogIn;

    private FirebaseAuth mAuth;
    private AlertDialog mDialog;

    @Override
    public AlertDialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View mView = inflater.inflate(R.layout.dialog_log_in, nullParent);

        etEmailToLogIn = mView.findViewById(R.id.et_email_log_in);
        etPasswordToLogIn = mView.findViewById(R.id.et_password_log_in);
        mAuth = FirebaseAuth.getInstance();

        TextView tvCreateAccount = mView.findViewById(R.id.tv_create_account);
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create dialog for create account
                AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(getActivity());
                final View createAccountView = inflater.inflate(R.layout.dialog_create_account, nullParent);

                final EditText etEmailSignUp = createAccountView.findViewById(R.id.et_email_sing_up);
                final EditText etPasswordSignUp = createAccountView.findViewById(R.id.et_password_sing_up);
                EditText etReEnterPasswordSignUp = createAccountView.findViewById(R.id.et_enter_again_password_sing_up);


                createAccountBuilder.setView(createAccountView)
                        .setMessage("Create an account")
                        .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //create user
                                mAuth.createUserWithEmailAndPassword(etEmailSignUp.getText().toString(), etPasswordSignUp.getText().toString())
                                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                // If sign in fails, display a message to the user. If sign in succeeds
                                                // the auth state listener will be notified and logic to handle the
                                                // signed in user can be handled in the listener.
                                                if (!task.isSuccessful()) {
                                                    Log.d(TAG, " Authentication failed.");
                                                }else{
                                                    Log.d(TAG, " Authentication is successfully.");
                                                }
                                            }
                                        });
                            }
                        })
                        .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });

        builder.setView(mView)
                .setMessage("Log in")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
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
                    } else {
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
