package com.faunog.m08_act02_conectwithxampp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseViewer extends AppCompatActivity {

    private TableLayout tableLayoutUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        toolbarNavigationFunction();
        connectVariableWithElements();
        fetchAndPopulateTableLayoutUsers();

    }

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

    private void connectVariableWithElements() {
        tableLayoutUsers = findViewById(R.id.tableLayoutUsers);
    }

    private void fetchAndPopulateTableLayoutUsers() {
        DatabaseControler.consultUsers()
                .thenAccept(document -> XMLConverter.convertXMLtoUsuariosMySQL(document)
                        .forEach(usuario -> {
                            @SuppressLint("InflateParams")
                            TableRow row = (TableRow) LayoutInflater.from(this)
                                    .inflate(R.layout.item_row_table_layout, null);

                            TextView usernameTextView = row.findViewById(R.id.username);
                            TextView passwordTextView = row.findViewById(R.id.password);
                            TextView dateTimeTextView = row.findViewById(R.id.dateTime);

                            usernameTextView.setText(usuario.usuario());
                            passwordTextView.setText(usuario.contrasena());
                            dateTimeTextView.setText(usuario.fecha_nacimiento());

                            int color = getColor(R.color.black);

                            usernameTextView.setTextColor(color);
                            passwordTextView.setTextColor(color);
                            dateTimeTextView.setTextColor(color);

                            tableLayoutUsers.addView(row);
                        }));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        List<String> dataToSave = getDataFromTableLayout();
        outState.putStringArrayList("data_key", new ArrayList<>(dataToSave));
    }

    private List<String> getDataFromTableLayout() {
        return IntStream.range(0, tableLayoutUsers.getChildCount())
                .mapToObj(i -> tableLayoutUsers.getChildAt(i))
                .filter(view -> view instanceof TableRow)
                .map(view -> (TableRow) view)
                .map(row -> {
                    TextView usernameTextView = row.findViewById(R.id.username);
                    TextView passwordTextView = row.findViewById(R.id.password);
                    TextView dateTimeTextView = row.findViewById(R.id.dateTime);

                    return usernameTextView.getText().toString() + ", " +
                            passwordTextView.getText().toString() + ", " +
                            dateTimeTextView.getText().toString();
                })
                .collect(Collectors.toList());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<String> savedData = savedInstanceState.getStringArrayList("data_key");
        assert savedData != null;
        repopulateTableLayout(savedData);
    }

    private void repopulateTableLayout(List<String> savedData) {
        tableLayoutUsers.removeAllViews();
        savedData.stream()
                .map(rowData -> rowData.split(", "))
                .filter(values -> values.length == 3)
                .forEach(values -> {
                    @SuppressLint("InflateParams")
                    TableRow row = (TableRow) LayoutInflater.from(this)
                            .inflate(R.layout.item_row_table_layout, null);

                    TextView usernameTextView = row.findViewById(R.id.username);
                    TextView passwordTextView = row.findViewById(R.id.password);
                    TextView dateTimeTextView = row.findViewById(R.id.dateTime);

                    usernameTextView.setText(values[0]);
                    passwordTextView.setText(values[1]);
                    dateTimeTextView.setText(values[2]);

                    int color = getColor(R.color.black);

                    usernameTextView.setTextColor(color);
                    passwordTextView.setTextColor(color);
                    dateTimeTextView.setTextColor(color);

                    tableLayoutUsers.addView(row);
                });
    }
}