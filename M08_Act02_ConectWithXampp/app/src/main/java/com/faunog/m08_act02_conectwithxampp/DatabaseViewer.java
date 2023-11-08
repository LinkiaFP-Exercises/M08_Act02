package com.faunog.m08_act02_conectwithxampp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class DatabaseViewer extends AppCompatActivity {

    private TableLayout tableLayoutUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        toolbarNavigationFunction();
        connectVariableWithElements();
        fetchAndPopulateUsers();

    }

    private void toolbarNavigationFunction() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        OpenActivities.toolbarGoToMainViewer(toolbar,this);
    }

    private void connectVariableWithElements() {
        tableLayoutUsers = findViewById(R.id.tableLayoutUsers);
    }

    private void fetchAndPopulateUsers() {
        DatabaseControler.consultUsers()
                .thenAccept(document -> {
                    List<UsuariosMySQL> usuariosMySQLList = XMLConverter.convertXMLtoUsuariosMySQL(document);
                    XMLConverter.populateTableLayoutUsers(tableLayoutUsers, usuariosMySQLList);
                });
    }

}