package com.faunog.m08_act02_conectwithxampp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLConverter {


    public static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static String catchStatusResponseFromXMLDocument(Document document) {
        NodeList listaItem = (NodeList) document.getElementsByTagName("respuesta");
        Element element = (Element) listaItem.item(0);
        return element.getElementsByTagName("estado").item(0).getTextContent();
    }

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
            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(tableLayout.getContext()).inflate(R.layout.item_row_table_layout, null);

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

