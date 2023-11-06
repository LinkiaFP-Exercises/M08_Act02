package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectVariableWithElements();
        applyListenersToButtonLogin();
    }

    private void applyListenersToButtonLogin() {
        loginButton.setOnClickListener((v) -> {
            try {
                final String[] response = ifHasTheNecessaryToConnect();
                handleResponse(response);
            } catch (ExecutionException | InterruptedException e) {
                handleException(e);
            }
        });
    }

    private String[] ifHasTheNecessaryToConnect() throws ExecutionException, InterruptedException {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        String messageStatus = validateCredentials(username, password);

        if (!messageStatus.equals("ok")) {
            Toast.makeText(getApplicationContext(), messageStatus, Toast.LENGTH_LONG).show();
        }

        return new String[]{messageStatus, username, password};
    }

    private String validateCredentials(String username, String password) {
        if (notAlphanumeric_NotNull(username)) {
            return "Username solo alfanumericos\na-z, A-Z, 0-9 o _";
        } else if (notBetween4And8digits(password)) {
            return "Contraseña: 4 a 8 digits\na-zA-Z0-9_-!¡*+,.@#€$%&?¿";
        } else if (networkNotAvailable()) {
            return "No hay conectividad,Conecte a una red móvil o wi-fi";
        } else if (serverNotAvailable()) {
            return "Servidor no disponible,\nIntente de nuevo más tarde.";
        } else if (databaseNotAvailable(username, password)) {
            return "Base de Datos no disponible,\nIntente de nuevo más tarde.";
        }
        return "ok";
    }

    private void handleResponse(String[] response) {
        if (response[0].equals("ok")) {
            connectThenOpenDatabaseViewer(response[1], response[2]);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
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

    private String catchStatusResponseFromXMLDocument(Document document) {
        NodeList listaItem = (NodeList) document.getElementsByTagName("respuesta");
        Element element = (Element) listaItem.item(0);
        return element.getElementsByTagName("estado").item(0).getTextContent();
    }

    private void OpenDatabaseViewer(String usuarioValido) {
        // Si el usuario es válido, abrir la nueva actividad
        if (usuarioValido.equals("ok")) {
            Intent intent = new Intent(this, DatabaseViewer.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean databaseNotAvailable(String user, String password) {
        try {
            return !DatabaseControler.isMySQLRunning(user, password).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in databaseNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;

    }

    private boolean serverNotAvailable() {
        try {
            return !DatabaseControler.isServerRunning().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in serverNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;

    }

    private boolean networkNotAvailable() {
        try {
            return !isNetworkAvailable().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error in networkNotAvailable:\n\n\n" + e.getMessage());
        }
        return true;
    }

    private CompletableFuture<Boolean> isNetworkAvailable() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
            return false;
        }, miExecutor);
    }

    private void connectThenOpenDatabaseViewer(String username, String password) {
        try {
            CompletableFuture<String> response = DatabaseControler.validateUser(username, password);
            Document document = convertStringToXMLDocument(response.get());
            String responseStatus = catchStatusResponseFromXMLDocument(document);
            OpenDatabaseViewer(responseStatus);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static final String TAG = "MainActivity";
}
