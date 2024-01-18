package com.faunog.m08_act02_conectwithxampp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase principal que maneja la actividad de inicio de sesión.
 * Esta clase permite a los usuarios ingresar su nombre de usuario y contraseña para iniciar sesión en la aplicación.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private MainLoginManager mainLoginManager;

    /**
     * Método que se llama cuando se crea la actividad.
     * Inicializa y configura la interfaz de usuario y los elementos necesarios.
     *
     * @param savedInstanceState La instancia anterior de la actividad, si está disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectVariableWithElements();
        applyListenersToButtonLogin();
    }

    /**
     * Conecta los elementos de la interfaz de usuario con las variables en la clase.
     * Establece referencias a los campos de texto de nombre de usuario y contraseña, así como al botón de inicio de sesión
     * además del gestor de inicio de sesión principal.
     */
    private void connectVariableWithElements() {
        usernameEditText = findViewById(R.id.login_user);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        mainLoginManager = new MainLoginManager(this);
    }

    /**
     * Aplica un listener al botón de inicio de sesión.
     * Cuando se hace clic en el botón, se intenta iniciar sesión utilizando el gestor de inicio de sesión principal.
     */
    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> tryLogin());
    }

    /**
     * Intenta iniciar sesión en la aplicación utilizando el nombre de usuario y la contraseña proporcionados.
     * Utiliza el gestor de inicio de sesión principal para llevar a cabo el proceso de inicio de sesión.
     */
    private void tryLogin() {
        mainLoginManager.login(usernameEditText, passwordEditText);
    }
}
