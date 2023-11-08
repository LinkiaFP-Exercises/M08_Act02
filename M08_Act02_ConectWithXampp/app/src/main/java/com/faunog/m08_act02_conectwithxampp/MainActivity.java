package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
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
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> {
            try {
                final String[] response = ifHasTheNecessaryToConnect();
                handleResponse(response);
            } catch (ExecutionException | InterruptedException e) {
                handleException(e);
            }
        });
    }

    private String[] ifHasTheNecessaryToConnect() throws ExecutionException, InterruptedException {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String messageStatus = validateCredentials(username, password);

        if (!messageStatus.equals("ok")) {
            Toast.makeText(getApplicationContext(), messageStatus, Toast.LENGTH_LONG).show();
        }

        return new String[]{messageStatus, username, password};
    }

    private String validateCredentials(String username, String password) {
        if (notAlphanumeric_NotNull(username)) {
            return "Username solo alfanumericos\na-z, A-Z, 0-9 o _";
        } else if (notBetween4And8digits(password)) {
            return "Contraseña: 4 a 8 digits\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
        } else if (networkNotAvailable()) {
            return "No hay conectividad,Conecte a una red móvil o wi-fi";
        } else if (serverNotAvailable()) {
            return "Servidor no disponible,\nIntente de nuevo más tarde.";
        } else if (databaseNotAvailable(username, password)) {
            return "Base de Datos no disponible,\nIntente de nuevo más tarde.";
        }
        return "ok";
    }

    private void handleResponse(String[] response) {
        if (response[0].equals("ok")) {
            connectThenOpenDatabaseViewer(response[1], response[2]);
        }
    }

    private void handleException(Exception e) {
        Log.e(TAG, "Error in applyListenersToButtonLogin:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }

    private boolean notAlphanumeric_NotNull(String input) {
        return input == null || !input.matches("\\w+");
    }

    private boolean notBetween4And8digits(String input) {
        final String regexPassword = "^[\\w!¡*+,.\\-@#€$%&?¿]+$";
        return input == null || input.length() < 4 || input.length() > 8 || !input.matches(regexPassword);
    }

    private boolean databaseNotAvailable(String user, String password) {
        try {
            return !DatabaseControler.isMySQLRunning(user, password).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in databaseNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;
    }

    private boolean serverNotAvailable() {
        try {
            return !DatabaseControler.isServerRunning().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in serverNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;
    }

    private boolean networkNotAvailable() {
        try {
            return !isNetworkAvailable().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error in networkNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;
    }

    private CompletableFuture<Boolean> isNetworkAvailable() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
            return false;
        }, miExecutor);
    }

    private void connectThenOpenDatabaseViewer(String username, String password) {
        DatabaseControler.validateUser(username, password).thenAccept(responseStatus -> {
            if (responseStatus.equals("ok")) OpenDatabaseViewer(responseStatus);
            else ifUserAndPassNotOkSaveFailedAttempt(username, password);
        });
    }

    private void OpenDatabaseViewer(String validLoginUserAndPass) {
        // Si el usuario es válido, abrir la nueva actividad
        if (validLoginUserAndPass.equals("ok")) {
            openActivityDatabaseViewer();
        } else {
            Toast.makeText(this, "Usuario o contraseña inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void openActivityDatabaseViewer() {
        Intent intent = new Intent(this, DatabaseViewer.class);
        startActivity(intent);
        finish();
    }

    private void ifUserAndPassNotOkSaveFailedAttempt(String username, String password) {
        boolean success = sqLiteManager.saveFailedAttempt(username, password);
        final String TAG_sqLiteFailedAccounts = "sqLiteFailedAccounts";

        if (success) Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt Saved");
        else Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt NOT Saved");

        openActivityFailedAttemptsViewer();
    }

    private void openActivityFailedAttemptsViewer() {
        Intent intent = new Intent(this, FailedAttemptsViewer.class);
        startActivity(intent);
        finish();
    }
}
