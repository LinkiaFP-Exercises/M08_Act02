package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class MainValidations {

    private static final String TAG = "MainActivity";

    public String[] ifHasTheNecessaryToConnect(String username, String password, Context context) {

        String messageStatus = validateCredentials(username, password, context);

        if (!messageStatus.equals("ok")) {
            Toast.makeText(context, messageStatus, Toast.LENGTH_LONG).show();
        }

        return new String[]{messageStatus, username, password};
    }

    private String validateCredentials(String username, String password, Context context) {
        if (notAlphanumeric(username)) {
            return "Username solo alfanumericos\na-z, A-Z, 0-9 o _";
        } else if (notBetween4And8digits(password)) {
            return "Contraseña: 4 a 8 dígitos\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
        } else if (!isNetworkAvailable(context)) {
            return "No hay conectividad, conecte a una red móvil o Wi-Fi";
        } else {
            try {
                if (!DatabaseControler.isServerRunning().get()) {
                    return "Servidor no disponible, intente de nuevo más tarde.";
                } else if (!DatabaseControler.isMySQLRunning(username, password).get()) {
                    return "Base de datos no disponible, intente de nuevo más tarde.";
                }
            } catch (ExecutionException | InterruptedException e) {
                handleException(e);
            }
        }
        return "ok";
    }

    private boolean notAlphanumeric(String input) {
        return input == null || !input.matches("\\w+");
    }

    private boolean notBetween4And8digits(String input) {
        final String regexPassword = "^[\\w!¡*+,.\\-@#€$%&?¿]+$";
        return input == null || input.length() < 4 || input.length() > 8 || !input.matches(regexPassword);
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    private void handleException(Exception e) {
        Log.e(TAG, "Error in validateCredentials:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }
}
