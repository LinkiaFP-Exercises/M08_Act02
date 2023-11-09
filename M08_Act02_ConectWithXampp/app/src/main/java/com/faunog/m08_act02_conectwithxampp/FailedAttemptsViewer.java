package com.faunog.m08_act02_conectwithxampp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Clase que permite ver los intentos de inicio de sesión fallidos y mostrarlos en una tabla.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class FailedAttemptsViewer extends AppCompatActivity {

    private TableLayout tableLayoutFailedAttempts;
    private SQLiteFailedAccounts sqLiteFailedAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_attempts);
        createSQLiteFailedAccounts();
        toolbarNavigationFunction();
        connectVariableWithElements();
        populateTableLayout();
        showToastUserOrPassInvalid();
    }

    /**
     * Configura la función de navegación de la barra de herramientas.
     */
    private void toolbarNavigationFunction() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        OpenActivities.toolbarGoToMainViewer(toolbar, this);
    }

    /**
     * Crea una instancia de la base de datos de intentos de inicio de sesión fallidos.
     */
    private void createSQLiteFailedAccounts() {
        sqLiteFailedAccounts = new SQLiteFailedAccounts(this);
    }

    /**
     * Conecta las variables con los elementos de la interfaz de usuario.
     */
    private void connectVariableWithElements() {
        tableLayoutFailedAttempts = findViewById(R.id.tableLayoutFailedAttempts);
    }

    /**
     * Llena la tabla de diseño con los datos de los intentos de inicio de sesión fallidos.
     * @see SQLiteFailedAccounts#getFailedAccounts() 
     * @see FailedAttemptsViewer#convertFailedLoginInRow(FailedLogin)
     */
    private void populateTableLayout() {
        sqLiteFailedAccounts.getFailedAccounts().forEach(this::convertFailedLoginInRow);

    }

    /**
     * Convierte un intento de inicio de sesión fallido en una fila de la tabla.
     *
     * @param failedLogin El intento de inicio de sesión fallido a convertir.
     */
    private void convertFailedLoginInRow(FailedLogin failedLogin) {
        @SuppressLint("InflateParams")
        TableRow row = (TableRow) LayoutInflater.from(this)
                .inflate(R.layout.item_row_table_layout, null);

        TextView usernameTextView = row.findViewById(R.id.username);
        TextView passwordTextView = row.findViewById(R.id.password);
        TextView dateTimeTextView = row.findViewById(R.id.dateTime);

        usernameTextView.setText(failedLogin.username());
        passwordTextView.setText(failedLogin.password());
        dateTimeTextView.setText(failedLogin.dateTime());

        tableLayoutFailedAttempts.addView(row);
    }

    /**
     * Muestra un mensaje emergente indicando que el usuario o la contraseña son inválidos.
     */
    private void showToastUserOrPassInvalid() {
        Toast.makeText(this, "Usuario o contraseña inválidos", Toast.LENGTH_SHORT).show();
    }
}