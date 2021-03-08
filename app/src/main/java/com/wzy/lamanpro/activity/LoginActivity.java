package com.wzy.lamanpro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.common.CommonActivity;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.SPUtility;

import static com.wzy.lamanpro.common.LaManApplication.isManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends CommonActivity implements OnClickListener {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private UserDaoUtils userDaoUtils;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        // Set up the login form.
    }

    private void initData() {
        UserDaoUtils userDaoUtils = new UserDaoUtils(this);
        if (userDaoUtils.queryUserSize("admin") == 0) {
            userDaoUtils.insertUserList(new Users((long) 1, "admin", "admin", "admin", "admin@laman.com", 1));
        }
        if (SPUtility.getSPBoolean(LoginActivity.this, "isAutoLogin")) {
            mAuthTask = new UserLoginTask(SPUtility.getUserId(this), SPUtility.getSPString(this, "password"));
            mAuthTask.execute((Void) null);
            checkbox.setChecked(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (!isEmailValid(account)) {
            mAccountView.setError(getString(R.string.error_invalid_email));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(account, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String account) {
        //TODO: Replace this with your own logic
        return account.length() > 2;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void initView() {
        userDaoUtils = new UserDaoUtils(this);

        checkbox = findViewById(R.id.checkbox);
        mAccountView = findViewById(R.id.account);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

//        login_progress = (ProgressBar) findViewById(R.id.login_progress);
//        login_progress.setOnClickListener(this);
//        account = (EditText) findViewById(R.id.account);
//        account.setOnClickListener(this);
//        password = (EditText) findViewById(R.id.password);
//        password.setOnClickListener(this);
//        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);
//        email_sign_in_button.setOnClickListener(this);
//        account_login_form = (LinearLayout) findViewById(R.id.account_login_form);
//        account_login_form.setOnClickListener(this);
//        login_form = (ScrollView) findViewById(R.id.login_form);
//        login_form.setOnClickListener(this);

        //调试用，快速测试全新安装之后的逻辑
        if (LaManApplication.testMode) {
            mAccountView.setText("admin");
            mPasswordView.setText("admin");
            checkbox.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:

                break;
        }
    }

//    private void submit() {
//        // validate
//        String accountString = account.getText().toString().trim();
//        if (TextUtils.isEmpty(accountString)) {
//            Toast.makeText(this, "accountString不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String passwordString = password.getText().toString().trim();
//        if (TextUtils.isEmpty(passwordString)) {
//            Toast.makeText(this, "passwordString不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // TODO validate success, do something
//
//
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mAccount;
        private final String mPassword;

        UserLoginTask(String account, String password) {
            //Log.d("Account", "account is:" + account + ",and password is:" + password);
            mAccount = account;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (userDaoUtils.queryUserSize(mAccount) == 0) {
                return -1;
            }
            // TODO: register the new account here.
            return mPassword.equalsIgnoreCase(userDaoUtils.queryUserPass(mAccount)) ? 0 : 1;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            showProgress(false);

            switch (success) {
                case 0:
                    SPUtility.putSPBoolean(LoginActivity.this, "isAutoLogin", checkbox.isChecked());
                    SPUtility.setUserId(LoginActivity.this, mAccount);
                    SPUtility.putSPString(LoginActivity.this, "password", mPassword);
                    isManager = userDaoUtils.queryUser(mAccount).getLevel() == 1;
                    SPUtility.putSPBoolean(LoginActivity.this, "manager", isManager);
                    finish();
                    Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    startActivity(intent);
                    break;
                case 1:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case -1:
                    mAccountView.setError(getString(R.string.error_unexist_account));
                    mAccountView.requestFocus();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
