package com.faunog.m08_act02_conectwithxampp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private MainLoginManager mainLoginManager;

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
        mainLoginManager = new MainLoginManager(this);
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> tryLogin());
    }

    private void tryLogin() {
        mainLoginManager.login(usernameEditText, passwordEditText);
    }

}
