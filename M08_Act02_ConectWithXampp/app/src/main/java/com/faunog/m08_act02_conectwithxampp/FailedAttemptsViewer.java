package com.faunog.m08_act02_conectwithxampp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

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
        populateListView();
    }

    private void toolbarNavigationFunction() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createSQLiteFailedAccounts() {
        sqLiteFailedAccounts = new SQLiteFailedAccounts(this);
    }

    private void connectVariableWithElements() {
        tableLayoutFailedAttempts = findViewById(R.id.tableLayoutFailedAttempts);
    }

    private void populateListView() {
        List<FailedLogin> failedAttemptsList = sqLiteFailedAccounts.getFailedAccounts();
        for (FailedLogin failedLogin : failedAttemptsList) {
            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.item_failed_login, null);

            TextView usernameTextView = row.findViewById(R.id.username);
            TextView passwordTextView = row.findViewById(R.id.password);
            TextView dateTimeTextView = row.findViewById(R.id.dateTime);

            usernameTextView.setText(failedLogin.username());
            passwordTextView.setText(failedLogin.password());
            dateTimeTextView.setText(failedLogin.dateTime());

            tableLayoutFailedAttempts.addView(row);
        }
    }
}