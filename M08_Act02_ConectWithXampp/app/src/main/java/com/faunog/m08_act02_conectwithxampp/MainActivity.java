package com.faunog.m08_act02_conectwithxampp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

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
        loginButton.setOnClickListener((v) -> {
            try {
                final String[] response = valid.ifHasTheNecessaryToConnect(usernameEditText, passwordEditText, this);
                handleResponse(response);
            } catch (ExecutionException | InterruptedException e) {
                handleException(e);
            }
        });
    }

    private void handleResponse(String[] response) {
        if (response[0].equals("ok")) {
            DatabaseControler.validateUser(response[1], response[2]).thenAccept(responseStatus -> {
                if (responseStatus.equals("ok")) OpenActivities.databaseViewer(this);
                else ifUserAndPassNotOkSaveFailedAttempt(response[1], response[2]);
            });
        }
    }

    private void handleException(Exception e) {
        Log.e(TAG, "Error in applyListenersToButtonLogin:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }

    private void ifUserAndPassNotOkSaveFailedAttempt(String username, String password) {
        boolean success = sqLiteManager.saveFailedAttempt(username, password);
        final String TAG_sqLiteFailedAccounts = "sqLiteFailedAccounts";

        if (success) Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt Saved");
        else Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt NOT Saved");

        OpenActivities.failedAttemptsViewer(this);
    }

}
