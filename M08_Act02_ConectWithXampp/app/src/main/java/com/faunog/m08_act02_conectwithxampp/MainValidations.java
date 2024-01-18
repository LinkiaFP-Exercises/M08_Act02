package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Clase que proporciona validaciones necesarias para el proceso de inicio de sesión.
 * Esta clase se encarga de verificar las credenciales del usuario y la disponibilidad de la red.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class MainValidations {

    private static final String TAG = "MainActivity";

    /**
     * Método que verifica si se tienen los elementos necesarios para conectarse.
     *
     * @param username El nombre de usuario proporcionado.
     * @param password La contraseña proporcionada.
     * @param context  El contexto de la aplicación.
     * @return Un array de cadenas que contiene el mensaje de estado, el nombre de usuario y la contraseña.
     */
    public String[] ifHasTheNecessaryToConnect(String username, String password, Context context) {

        String messageStatus = validateCredentials(username, password, context);

        if (!messageStatus.equals("ok")) {
            Toast.makeText(context, messageStatus, Toast.LENGTH_LONG).show();
        }

        return new String[]{messageStatus, username, password};
    }

    /**
     * Método privado que valida las credenciales del usuario.
     *
     * @param username El nombre de usuario proporcionado.
     * @param password La contraseña proporcionada.
     * @param context  El contexto de la aplicación.
     * @return Un mensaje de estado que indica si las credenciales son válidas o no.
     * @see MainValidations#notAlphanumeric(String)
     * @see MainValidations#notBetween4And8digits(String)
     * @see MainValidations#isNetworkAvailable(Context)
     * @see DatabaseControler#isServerRunning()
     * @see DatabaseControler#isMySQLRunning(String, String)
     */
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

    /**
     * Método privado que verifica si la cadena no contiene caracteres alfanuméricos.
     *
     * @param input La cadena a ser validada.
     * @return true si la cadena no contiene caracteres alfanuméricos, de lo contrario, false.
     */
    private boolean notAlphanumeric(String input) {
        return input == null || !input.matches("\\w+");
    }

    /**
     * Método privado que verifica si la cadena no está entre 4 y 8 caracteres de longitud.
     *
     * @param input La cadena a ser validada.
     * @return true si la cadena no está entre 4 y 8 caracteres de longitud, de lo contrario, false.
     */
    private boolean notBetween4And8digits(String input) {
        final String regexPassword = "^[\\w!¡*+,.\\-@#€$%&?¿]+$";
        return input == null || input.length() < 4 || input.length() > 8 || !input.matches(regexPassword);
    }

    /**
     * Método privado que verifica si hay conectividad de red disponible.
     *
     * @param context El contexto de la aplicación.
     * @return true si hay conectividad de red disponible, de lo contrario, false.
     */
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    /**
     * Método privado que maneja excepciones generadas durante la validación de credenciales.
     *
     * @param e La excepción capturada durante la validación.
     */
    private void handleException(Exception e) {
        Log.e(TAG, "Error in validateCredentials:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }
}
