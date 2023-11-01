package com.faunog.m08_act02_conectwithxampp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectVariableWithElements();
        applyListenersToButtonLogin();
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> verifyIfHasTheNecessaryToConnectOrNotifyUser());
    }

    private void verifyIfHasTheNecessaryToConnectOrNotifyUser() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String messageError;
        if (notAlphanumeric_NotNull(username)) {
            messageError = "Username solo a-z, A-Z, 0-9 o _";
            Toast.makeText(getApplicationContext(), messageError, Toast.LENGTH_LONG).show();
        } else if (notBetween4And8digits(password)) {
            messageError = "Contraseña: 4 a 8 digits\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
            Toast.makeText(getApplicationContext(), messageError, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "ok!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean notAlphanumeric_NotNull(String input) {
        return input == null || !input.matches("\\w+");
    }

    private boolean notBetween4And8digits(String input) {
        final String regexPassword = "^[\\w!¡*+,.\\-@#€$%&?¿]+$";
        return input == null || input.length() < 4 || input.length() > 8 || !input.matches(regexPassword);
    }

    private void connectVariableWithElements() {
        usernameEditText = findViewById(R.id.login_user);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
    }

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
}