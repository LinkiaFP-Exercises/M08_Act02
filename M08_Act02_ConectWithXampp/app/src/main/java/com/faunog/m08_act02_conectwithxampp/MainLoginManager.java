package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

/**
 * Clase que administra el inicio de sesión principal y la lógica asociada.
 * Esta clase se encarga de realizar validaciones y acciones relacionadas con el inicio de sesión principal de la aplicación.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class MainLoginManager {
    private final MainValidations valid;
    private final SQLiteFailedAccounts sqLiteManager;
    private final Context context;
    private static final String TAG = "MainActivity";

    /**
     * Constructor de la clase MainLoginManager.
     *
     * @param context El contexto de la aplicación.
     */
    public MainLoginManager(Context context) {
        this.context = context;
        sqLiteManager = SQLiteFailedAccounts.createManager(context);
        valid = new MainValidations();
    }

    /**
     * Método que maneja el intento de inicio de sesión.
     * {@code response} es un array de cadenas que contiene la respuesta del servidor.
     * La posición 0 contiene el mensaje de estado, la posición 1 contiene el nombre de usuario
     * y la posición 2 contiene la contraseña.
     *
     * @param usernameEditText El campo de texto para el nombre de usuario.
     * @param passwordEditText El campo de texto para la contraseña.
     */
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

    /**
     * Método que maneja la respuesta obtenida al intentar iniciar sesión.
     * Este método realiza acciones específicas dependiendo de la respuesta recibida.
     *
     * @param response Un array de cadenas que contiene la respuesta del servidor.
     *                 La posición 0 contiene el mensaje de estado, la posición 1 contiene el nombre de usuario
     *                 y la posición 2 contiene la contraseña.
     */
    private void handleResponse(String[] response) {
        if (isLoginSuccessful(response)) {
            DatabaseControler.validateUser(response).thenAccept(responseStatus -> {
                if (responseStatus.equals("ok")) OpenActivities.databaseViewer(context);
                else sqLiteManager.ifUserAndPassNotOkSaveFailedAttempt(response, context);
            });
        }
    }

    /**
     * Método que verifica si el inicio de sesión fue exitoso.
     * Este método comprueba si la respuesta indica un inicio de sesión exitoso.
     *
     * @param response La respuesta recibida al intentar iniciar sesión.
     * @return Devuelve verdadero si el inicio de sesión fue exitoso, falso en caso contrario.
     */
    private boolean isLoginSuccessful(String[] response) {
        return response[0].equals("ok");
    }

    /**
     * Método que maneja las excepciones durante el inicio de sesión.
     * Este método registra el error en los registros y lanza una excepción.
     *
     * @param e La excepción capturada durante el inicio de sesión.
     */
    private void handleException(Exception e) {
        Log.e(TAG, "Error in applyListenersToButtonLogin:\n\n\n" + e.getMessage());
        throw new RuntimeException(e);
    }
}
