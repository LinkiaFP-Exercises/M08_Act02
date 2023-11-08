package com.faunog.m08_act02_conectwithxampp;

import android.util.Log;

import org.w3c.dom.DOMException;
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
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Log.e(TAG, "Error in convertStringToXMLDocument:\n\n\n" + e.getMessage());
            return null;
        }
    }

    public static String catchStatusResponseFromXMLDocument(Document document) {
        try {
            NodeList listaItem = document.getElementsByTagName("respuesta");
            Element element = (Element) listaItem.item(0);
            return element.getElementsByTagName("estado").item(0).getTextContent();
        } catch (NullPointerException | DOMException e) {
            Log.e(TAG, "Error in catchStatusResponseFromXMLDocument:\n\n\n" + e.getMessage());
            return null;
        }
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

    private static final String TAG = "XMLConverter";
}

