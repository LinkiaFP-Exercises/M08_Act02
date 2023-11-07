package com.faunog.m08_act02_conectwithxampp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class FailedAttemptsViewer extends AppCompatActivity {

    private ListView listView;
    private List<FailedLogin> failedAttemptsList;
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
        listView = findViewById(R.id.listViewFailedAttempts);
    }

    private void populateListView() {
        failedAttemptsList = sqLiteFailedAccounts.getFailedAccounts();
        FailedLoginAdapter adapter = new FailedLoginAdapter(this, failedAttemptsList);
        listView.setAdapter(adapter);
    }
}