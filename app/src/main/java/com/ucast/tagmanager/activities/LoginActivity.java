package com.ucast.tagmanager.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.ucast.tagmanager.MainActivity;
import com.ucast.tagmanager.R;
import com.ucast.tagmanager.entity.LoginMSg;
import com.ucast.tagmanager.tools.HttpRequest;
import com.ucast.tagmanager.tools.MyTools;
import com.ucast.tagmanager.tools.SavePasswd;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;




/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.

    private TextInputLayout userNameInput;
    private TextInputLayout passwordInput;

    private EditText mUserName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SavePasswd save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        MyTools.setRoat(this);

        save = SavePasswd.getInstace();
        String info = save.get(MyTools.TOKEN);

//        if (!info.equals("")){
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }

        String login_id=save.get(MyTools.LOGIN_ID);
        String password=save.get(MyTools.PASSWORD);




        // Set up the login form.
        userNameInput = (TextInputLayout) findViewById(R.id.username);
        passwordInput = (TextInputLayout) findViewById(R.id.password);

        mUserName = userNameInput.getEditText();
        mPasswordView = passwordInput.getEditText();


//        if (!login_id.equals("")){
//            mUserName.setText(login_id);
//        }
//
//        if (!password.equals("")){
//            mPasswordView.setText(password);
//        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 5) {
                    userNameInput.setError(getResources().getString(R.string.username_morethan_six));
                    userNameInput.setErrorEnabled(true);
                } else {
                    userNameInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 5) {
                    passwordInput.setError(getResources().getString(R.string.password_morethan_six));
                    passwordInput.setErrorEnabled(true);
                } else {
                    passwordInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private static final String TAG = "LoginActivity";

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        showProgress(true);
        final String login_id = mUserName.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();

        RequestParams requestParams = new RequestParams(HttpRequest.LOGIN_URL);
        requestParams.addBodyParameter("Username", login_id);
        requestParams.addBodyParameter("Password", password);

//        requestParams.addHeader("Authorization","Basic " + info);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LoginMSg login = JSON.parseObject(result, LoginMSg.class);
//                showDialog("result:" + login.getResult() + "msg:" + login.getMsg()
//                        + "loginid:" + login.getServiceman().getLogin_id() +
//                        "emp_name" + login.getServiceman().getEmp_name()
//                );
                if (login.getMsgType().equals("1")) {
                    //请求成功
                    save.save(MyTools.TOKEN, login.getInfo());
                    save.save(MyTools.LOGIN_ID, login_id);
                    save.save(MyTools.PASSWORD, password);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    showDialog(login.getInfo());
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showDialog(getResources().getString(R.string.requesterror));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                showProgress(false);
            }
        });


//        startActivity(new Intent(this, QuerryActivity.class));
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

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


    public void showDialog(String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setPositiveButton(this.getResources().getString(R
                .string.queding), null).create();
        alertDialog.setTitle(this.getResources().getString(R.string.tishi));
        alertDialog.setMessage(s);
        alertDialog.show();
    }
}

