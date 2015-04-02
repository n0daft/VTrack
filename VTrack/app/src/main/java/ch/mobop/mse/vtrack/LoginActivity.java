package ch.mobop.mse.vtrack;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;

/**
 * Created by Simon on 24.03.2015.
 */
public class LoginActivity extends FragmentActivity {
    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";

    private String mUsername;
    private String mPassword;

    private EditText mUserView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private TextView dialogError;
    private ProgressDialog mDialog;

    //Password Dialog
    final Context context = this;

    private RequestToken mSignupOrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.dialog_login));

        // Todo remove this eventually
        getActionBar().hide();

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        ColorDrawable color = new ColorDrawable(sharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        getActionBar().setBackgroundDrawable(color);

        if (savedInstanceState!=null){
            mSignupOrLogin = savedInstanceState.getParcelable(SIGNUP_TOKEN_KEY);
        }

        mUserView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        findViewById(R.id.register_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get dailog_password.xml view
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.dialog_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText passwordCheck = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        //Do nothing here because we override this button later to change the close behaviour.
                                        //However, we still need this because on older versions of Android unless we
                                        //pass a handler the button doesn't get instantiated
                                        //result.setText(userInput.getText());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                //Overwrite the OK button
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        if(passwordCheck.getText().toString().equals(mPasswordView.getText().toString())){
                            alertDialog.dismiss();
                            attemptLogin(true);
                        }else{
                            dialogError = (TextView) promptsView.findViewById(R.id.txtError);
                            dialogError.setText("Password doesn't match");
                        }
                    }
                });

            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attemptLogin(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSignupOrLogin!=null){
            //showProgress(false);
            if (mDialog.isShowing()){
                mDialog.dismiss();
            }
            mSignupOrLogin.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSignupOrLogin!=null){
            if(!mDialog.isShowing())mDialog.show();
            mSignupOrLogin.resume(onComplete);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignupOrLogin!=null){
            outState.putParcelable(SIGNUP_TOKEN_KEY,mSignupOrLogin);
        }
    }

    private void completeLogin(boolean success){
        if (mDialog.isShowing()){
            mDialog.dismiss();
        }
        mSignupOrLogin = null;
        if (success) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void attemptLogin(boolean newUser) {
        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUserView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            signupWithBaasBox(newUser);
        }
    }

    private void signupWithBaasBox(boolean newUser){
        //todo 3.1
        BaasUser user = BaasUser.withUserName(mUsername);
        user.setPassword(mPassword);
        if (newUser) {
            mDialog.setMessage(getString(R.string.dialog_signup));
            if(!mDialog.isShowing())mDialog.show();
            mSignupOrLogin=user.signup(onComplete);
        } else {
            mDialog.setMessage(getString(R.string.dialog_login));
            if(!mDialog.isShowing())mDialog.show();
            mSignupOrLogin=user.login(onComplete);
        }
    }

    //todo 3.2
    private final BaasHandler<BaasUser> onComplete =
            new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {

                    mSignupOrLogin = null;
                    if (result.isFailed()){
                        Log.d("ERROR","ERROR",result.error());
                    }
                    completeLogin(result.isSuccess());
                }
            };


}



