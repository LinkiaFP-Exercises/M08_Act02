package com.faunog.m08_act02_conectwithxampp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private MainValidations valid;
    private SQLiteFailedAccounts sqLiteManager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectVariableWithElements();
        applyListenersToButtonLogin();
    }

    private void connectVariableWithElements() {
        usernameEditText = findViewById(R.id.login_user);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        sqLiteManager = SQLiteFailedAccounts.createManager(this);
        valid = new MainValidations();
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> tryLogin());
    }

    private void tryLogin() {
        try {
            final String[] response = valid.ifHasTheNecessaryToConnect(usernameEditText, passwordEditText, this);
            handleResponse(response);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleResponse(String[] response) {
        if (isLoginSuccessful(response)) {
            DatabaseControler.validateUser(response).thenAccept(responseStatus -> {
                if (responseStatus.equals("ok")) OpenActivities.databaseViewer(this);
                else ifUserAndPassNotOkSaveFailedAttempt(response);
            });
        }
    }

    private boolean isLoginSuccessful(String[] response) {
        return response[0].equals("ok");
    }

    private void handleException(Exception e) {
        Log.e(TAG, "Error in applyListenersToButtonLogin:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }

    private void ifUserAndPassNotOkSaveFailedAttempt(String[] statusUserPass) {
        boolean success = sqLiteManager.saveFailedAttempt(statusUserPass[1], statusUserPass[2]);
        final String TAG_sqLiteFailedAccounts = "sqLiteFailedAccounts";

        if (success) Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt Saved");
        else Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt NOT Saved");

        OpenActivities.failedAttemptsViewer(this);
    }
}
