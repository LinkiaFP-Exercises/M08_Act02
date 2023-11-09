package com.faunog.m08_act02_conectwithxampp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        DatabaseControler.consultUsers().thenAccept(document -> {
            XMLConverter.convertXMLtoUsuariosMySQL(document)
                    .forEach(this::convertUsuariosMySQLinRow);
        });

    }

    private void convertUsuariosMySQLinRow(UsuariosMySQL usuario) {
        @SuppressLint("InflateParams")
        TableRow row = (TableRow) LayoutInflater.from(this)
                .inflate(R.layout.item_row_table_layout, null);

        TextView usernameTextView = row.findViewById(R.id.username);
        TextView passwordTextView = row.findViewById(R.id.password);
        TextView dateTimeTextView = row.findViewById(R.id.dateTime);

        usernameTextView.setText(usuario.usuario());
        passwordTextView.setText(usuario.contrasena());
        dateTimeTextView.setText(usuario.fecha_nacimiento());

        tableLayoutUsers.addView(row);
    }

}