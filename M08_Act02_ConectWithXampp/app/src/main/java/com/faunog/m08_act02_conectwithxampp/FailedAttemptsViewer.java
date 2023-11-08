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

    private void toolbarNavigationFunction() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        OpenActivities.toolbarGoToMainViewer(toolbar, this);
    }

    private void createSQLiteFailedAccounts() {
        sqLiteFailedAccounts = new SQLiteFailedAccounts(this);
    }

    private void connectVariableWithElements() {
        tableLayoutFailedAttempts = findViewById(R.id.tableLayoutFailedAttempts);
    }

    private void populateTableLayout() {
        sqLiteFailedAccounts.getFailedAccounts().forEach(this::convertFailedLoginInRow);

    }

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

    private void showToastUserOrPassInvalid() {
        Toast.makeText(this, "Usuario o contraseña inválidos", Toast.LENGTH_SHORT).show();
    }
}