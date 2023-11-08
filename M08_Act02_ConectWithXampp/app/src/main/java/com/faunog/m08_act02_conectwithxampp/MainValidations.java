package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainValidations {

    private static final String TAG = "MainActivity";

    public String[] ifHasTheNecessaryToConnect(EditText usernameEditText, EditText passwordEditText, Context context) throws ExecutionException, InterruptedException {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String messageStatus = validateCredentials(username, password, context);

        if (!messageStatus.equals("ok")) {
            Toast.makeText(context, messageStatus, Toast.LENGTH_LONG).show();
        }

        return new String[]{messageStatus, username, password};
    }

    private String validateCredentials(String username, String password, Context context) {
        if (notAlphanumeric_NotNull(username)) {
            return "Username solo alfanumericos\na-z, A-Z, 0-9 o _";
        } else if (notBetween4And8digits(password)) {
            return "Contraseña: 4 a 8 digits\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
        } else if (networkNotAvailable(context)) {
            return "No hay conectividad,Conecte a una red móvil o wi-fi";
        } else if (serverNotAvailable()) {
            return "Servidor no disponible,\nIntente de nuevo más tarde.";
        } else if (databaseNotAvailable(username, password)) {
            return "Base de Datos no disponible,\nIntente de nuevo más tarde.";
        }
        return "ok";
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

    private boolean networkNotAvailable(Context context) {
        try {
            return !isNetworkAvailable(context).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error in networkNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;
    }

    private static CompletableFuture<Boolean> isNetworkAvailable(Context context) {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
            return false;
        }, miExecutor);
    }
}
