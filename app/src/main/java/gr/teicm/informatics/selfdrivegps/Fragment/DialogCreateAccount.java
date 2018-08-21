package gr.teicm.informatics.selfdrivegps.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.teicm.informatics.selfdrivegps.Controller.Controller;
import gr.teicm.informatics.selfdrivegps.R;

public class DialogCreateAccount extends  android.app.DialogFragment {
    private final String TAG = "DialogCreateAccount";
    private Controller controller = new Controller();
    private FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create dialog for create account
        final AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null; //To override the warning about null
        final View createAccountView = inflater.inflate(R.layout.dialog_create_account, nullParent);

        mAuth = FirebaseAuth.getInstance();

        createAccountBuilder.setView(createAccountView)
                .setMessage("Create an account")
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogLogIn dialogLogIn = new DialogLogIn();
                        dialogLogIn.show(controller.getAppFragmentManager(), "Return to Log In");
                        dialogLogIn.setCancelable(false);
                        dismiss();
                    }
                });

        final AlertDialog mDialog = createAccountBuilder.create();
        mDialog.show();
        mDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etEmailSignUp = createAccountView.findViewById(R.id.et_email_sing_up);
                EditText etPasswordSignUp = createAccountView.findViewById(R.id.et_password_sing_up);
                EditText etReEnterPasswordSignUp = createAccountView.findViewById(R.id.et_enter_again_password_sing_up);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //To be sure that all fills is not empty
                    Boolean allChecksToBeSureThatAllFillIsNotEmpty = (TextUtils.isEmpty(etEmailSignUp.getText().toString())
                            || TextUtils.isEmpty(etPasswordSignUp.getText().toString())
                            || TextUtils.isEmpty(etReEnterPasswordSignUp.getText().toString()));

                    //To be sure that password contains more than 6 chars
                    Boolean checkIfUserUsePasswordWithMoreThanFiveElements = etPasswordSignUp.getText().toString().length() >= 6;

                    //To be sure that etPasswordSingUp and etReEnterPasswordSignUp are equal
                    Boolean checkIfPasswordAndReEnterMatch = etPasswordSignUp.getText().toString().equals(etReEnterPasswordSignUp.getText().toString());

                    if (TextUtils.isEmpty(etEmailSignUp.getText().toString())) {
                        Toast.makeText(getContext(), "Email fill is empty !", Toast.LENGTH_SHORT).show();
                    }else if(TextUtils.isEmpty(etPasswordSignUp.getText().toString())) {
                        Toast.makeText(getContext(), "Password fill is empty !", Toast.LENGTH_SHORT).show();
                    }else if(TextUtils.isEmpty(etReEnterPasswordSignUp.getText().toString())) {
                        Toast.makeText(getContext(), "Please confirm password !", Toast.LENGTH_SHORT).show();
                    }else if(!allChecksToBeSureThatAllFillIsNotEmpty && !checkIfUserUsePasswordWithMoreThanFiveElements){
                        Toast.makeText(getContext(), "Password must be more than 6 characters !", Toast.LENGTH_SHORT).show();
                    }else if(!checkIfPasswordAndReEnterMatch) {
                        Toast.makeText(getContext(), "Password and re-enter of password isn't the same ! ", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmailSignUp.getText().toString()).matches()){
                        //To be sure that email is in right Form
                        Toast.makeText(getContext(), "Email form is wrong !", Toast.LENGTH_SHORT).show();
                    }else{
                        //create user
                        mAuth.createUserWithEmailAndPassword(etEmailSignUp.getText().toString(), etPasswordSignUp.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getContext(), " Authentication failed.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), " Authentication is successfully.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Creation of account was succeed");
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
}
