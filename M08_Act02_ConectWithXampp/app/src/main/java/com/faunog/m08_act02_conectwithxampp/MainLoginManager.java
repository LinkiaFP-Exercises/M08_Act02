package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

public class MainLoginManager {
    private final MainValidations valid;
    private final SQLiteFailedAccounts sqLiteManager;
    private final Context context;
    private static final String TAG = "MainActivity";

    public MainLoginManager(Context context) {
        this.context = context;
        sqLiteManager = SQLiteFailedAccounts.createManager(context);
        valid = new MainValidations();
    }

    public void login(EditText usernameEditText, EditText passwordEditText) {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        try {
            final String[] response = valid.ifHasTheNecessaryToConnect(username, password, context);
            handleResponse(response);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleResponse(String[] response) {
        if (isLoginSuccessful(response)) {
            DatabaseControler.validateUser(response).thenAccept(responseStatus -> {
                if (responseStatus.equals("ok")) OpenActivities.databaseViewer(context);
                else sqLiteManager.ifUserAndPassNotOkSaveFailedAttempt(response, context);
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
}
