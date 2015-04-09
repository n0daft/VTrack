package ch.mobop.mse.vtrack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;

/**
 * Provides methods for the login process.
 * Created by Simon on 24.03.2015.
 */
public class LoginActivity extends FragmentActivity {

    private String mUsername;
    private String mPassword;
    private EditText mTxtUser;
    private EditText mTxtPassword;
    private ProgressDialog mDialog;

    private RequestToken mSignupOrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.dialog_login));

        // Hide the actionbar and keyboard for stylistic reasons.
        getActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        ColorDrawable color = new ColorDrawable(sharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        getActionBar().setBackgroundDrawable(color);

        if (savedInstanceState!=null){
            mSignupOrLogin = savedInstanceState.getParcelable(Constants.SIGNUP_TOKEN_KEY);
        }

        mTxtUser = (EditText) findViewById(R.id.email);
        mTxtPassword = (EditText) findViewById(R.id.password);

        findViewById(R.id.register_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSignupOrLogin!=null){
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
        if (mSignupOrLogin!=null) {
            outState.putParcelable(Constants.SIGNUP_TOKEN_KEY, mSignupOrLogin);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
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
            mTxtPassword.setError(getString(R.string.error_incorrect_password));
            mTxtPassword.requestFocus();
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mTxtUser.setError(null);
        mTxtPassword.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mTxtUser.getText().toString();
        mPassword = mTxtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mTxtPassword.setError(getString(R.string.error_field_required));
            focusView = mTxtPassword;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mTxtPassword.setError(getString(R.string.error_invalid_password));
            focusView = mTxtPassword;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(mUsername)) {
            mTxtUser.setError(getString(R.string.error_field_required));
            focusView = mTxtUser;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            signupWithBaasBox();
        }
    }

    private void signupWithBaasBox(){
        BaasUser user = BaasUser.withUserName(mUsername);
        user.setPassword(mPassword);
        mDialog.setMessage(getString(R.string.dialog_login));
        if(!mDialog.isShowing())mDialog.show();
        mSignupOrLogin=user.login(onComplete);
    }

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



