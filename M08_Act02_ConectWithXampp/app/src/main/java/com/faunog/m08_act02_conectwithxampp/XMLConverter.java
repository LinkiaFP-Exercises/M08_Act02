package com.faunog.m08_act02_conectwithxampp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XMLConverter {

    public static List<UsuariosMySQL> convertXMLtoUsuariosMySQL(Document document) {
        List<UsuariosMySQL> usuariosMySQLList = new ArrayList<>();

        if (document != null) {
            NodeList usuariosList = document.getElementsByTagName("usuario");

            for (int i = 0; i < usuariosList.getLength(); i++) {
                Element usuario = (Element) usuariosList.item(i);

                String nombre = usuario.getElementsByTagName("nombre").item(0).getTextContent();
                String contrasena = usuario.getElementsByTagName("contrasena").item(0).getTextContent();
                String fechaNacimiento = usuario.getElementsByTagName("fecha_nacimiento").item(0).getTextContent();

                usuariosMySQLList.add(new UsuariosMySQL(nombre, contrasena, fechaNacimiento));
            }
        }

        return usuariosMySQLList;
    }

    public static void populateTableLayoutUsers(TableLayout tableLayout, List<UsuariosMySQL> usuariosMySQLList) {
        for (UsuariosMySQL usuario : usuariosMySQLList) {
            @SuppressLint("InflateParams")
            TableRow row = (TableRow) LayoutInflater.from(tableLayout.getContext())
                    .inflate(R.layout.item_row_table_layout, null);

            TextView usernameTextView = row.findViewById(R.id.username);
            TextView passwordTextView = row.findViewById(R.id.password);
            TextView dateTimeTextView = row.findViewById(R.id.dateTime);

            usernameTextView.setText(usuario.usuario());
            passwordTextView.setText(usuario.contrasena());
            dateTimeTextView.setText(usuario.fecha_nacimiento());

            tableLayout.addView(row);
        }
    }
}

