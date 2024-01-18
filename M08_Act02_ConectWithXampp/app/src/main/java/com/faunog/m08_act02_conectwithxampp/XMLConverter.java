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

/**
 * Esta clase proporciona métodos para convertir cadenas en documentos XML y viceversa, así como para manejar y convertir elementos XML a objetos de tipo UsuariosMySQL.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class XMLConverter {

    /**
     * Convierte una cadena XML en un documento XML.
     *
     * @param xmlString La cadena XML a convertir.
     * @return Un objeto Document que representa el documento XML.
     */
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

    /**
     * Captura el estado de respuesta de un documento XML.
     *
     * @param document El documento XML.
     * @return El estado de la respuesta.
     */
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

    /**
     * Convierte un documento XML en una lista de objetos UsuariosMySQL.
     *
     * @param document El documento XML a convertir.
     * @return Una lista de objetos UsuariosMySQL.
     * @see XMLConverter#getElementTextContent(Element, String)
     */
    public static List<UsuariosMySQL> convertXMLtoUsuariosMySQL(Document document) {
        List<UsuariosMySQL> usuariosMySQLList = new ArrayList<>();

        if (document != null) {
            NodeList usuariosList = document.getElementsByTagName("usuario");
            for (int i = 0; i < usuariosList.getLength(); i++) {
                Element usuario = (Element) usuariosList.item(i);

                try {
                    String nombre = getElementTextContent(usuario, "nombre");
                    String contrasena = getElementTextContent(usuario, "contrasena");
                    String fechaNacimiento = getElementTextContent(usuario, "fecha_nacimiento");

                    usuariosMySQLList.add(new UsuariosMySQL(nombre, contrasena, fechaNacimiento));
                } catch (NullPointerException | DOMException e) {
                    Log.e(TAG, "Error in convertXMLtoUsuariosMySQL:\n\n\n" + e.getMessage());
                }
            }
        }
        return usuariosMySQLList;
    }

    /**
     * Obtiene el contenido de texto de un elemento específico en el documento XML.
     *
     * @param element  El elemento XML.
     * @param tagName  El nombre de la etiqueta del elemento.
     * @return El contenido de texto del elemento.
     */
    private static String getElementTextContent(Element element, String tagName) {
        try {
            return element.getElementsByTagName(tagName).item(0).getTextContent();
        } catch (NullPointerException | DOMException e) {
            Log.e(TAG, "Error in getElementTextContent:\n\n\n" + e.getMessage());
            return "";
        }
    }

    private static final String TAG = "XMLConverter";
}
