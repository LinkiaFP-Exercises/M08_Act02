package com.faunog.m08_act02_conectwithxampp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectVariableWithElements();
        applyListenersToButtonLogin();
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> verifyIfHasTheNecessaryToConnectOrNotifyUser());
    }

    private void verifyIfHasTheNecessaryToConnectOrNotifyUser() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String messageError;
        if (notAlphanumeric_NotNull(username)) {
            messageError = "Username solo a-z, A-Z, 0-9 o _";
            Toast.makeText(getApplicationContext(), messageError, Toast.LENGTH_LONG).show();
        } else if (notBetween4And8digits(password)) {
            messageError = "Contraseña: 4 a 8 digits\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
            Toast.makeText(getApplicationContext(), messageError, Toast.LENGTH_LONG).show();
        } else {
            try {
                CompletableFuture<String> response = ValidateUser.validateAsync(username, password);
                Document document = convertStringToXMLDocument(response.get());
                String responseStatus = catchStatusResponseFromXMLDocument(document);
                abrirNuevaActividad(responseStatus);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            //Toast.makeText(getApplicationContext(), "ok!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean notAlphanumeric_NotNull(String input) {
        return input == null || !input.matches("\\w+");
    }

    private boolean notBetween4And8digits(String input) {
        final String regexPassword = "^[\\w!¡*+,.\\-@#€$%&?¿]+$";
        return input == null || input.length() < 4 || input.length() > 8 || !input.matches(regexPassword);
    }

    private void connectVariableWithElements() {
        usernameEditText = findViewById(R.id.login_user);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
    }

    private static Document convertStringToXMLDocument(String xmlString) {
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

    private String catchStatusResponseFromXMLDocument(Document document) {
        NodeList listaItem = (NodeList) document.getElementsByTagName("respuesta");
        Element element = (Element) listaItem.item(0);
        return element.getElementsByTagName("estado").item(0).getTextContent();
    }

    private void abrirNuevaActividad(String usuarioValido) {
        // Si el usuario es válido, abrir la nueva actividad
        if (usuarioValido.equals("ok")) {
            Intent intent = new Intent(this, DatabaseViewer.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
}
