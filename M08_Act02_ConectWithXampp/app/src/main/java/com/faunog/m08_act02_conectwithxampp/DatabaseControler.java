package com.faunog.m08_act02_conectwithxampp;

import android.util.Log;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DatabaseControler {

    static CompletableFuture<Boolean> isServerRunning() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = "http://192.168.1.113";
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                final int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                Log.e(TAG, "Error in isServerRunning:\n\n\n" + e.getMessage());
                return false;
            }
        }, miExecutor);
    }

    static CompletableFuture<Boolean> isMySQLRunning(String user, String password) {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = connectDatabase(user, password);
                return connection != null && connection.isValid(100);
            } catch (SQLException | ClassNotFoundException e) {
                Log.e(TAG, "Error in isMySQLRunning:\n\n\n" + e.getMessage());
                return false;
            }
        }, miExecutor);
    }

    private static Connection connectDatabase(String user, String password) throws SQLException, ClassNotFoundException {
        final String urlJDBC = "jdbc:mysql://192.168.1.113:3307/m08_act02";
        final String classJDBC = "com.mysql.jdbc.Driver";
        Connection conn;

        Class.forName(classJDBC);
        conn = DriverManager.getConnection(urlJDBC, user, password);

        return conn;
    }


    static CompletableFuture<String> validateUser(String[] statusUserPass) {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            String response = null;
            String url = "http://192.168.1.113/validacuenta2.php";

            try {
                HttpURLConnection connection = createHTTPConnection(url);
                sendDataToServer(connection, statusUserPass);
                response = processServerResponse(connection);
            } catch (Exception e) {
                Log.e(TAG, "Error in validateUser:\n\n\n" + e.getMessage());
            }

            return response;
        }, miExecutor);
    }

    private static HttpURLConnection createHTTPConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    private static void sendDataToServer(HttpURLConnection connection, String[] statusUserPass) throws IOException {
        String datos = "usuario=" + statusUserPass[1] + "&contrasena=" + statusUserPass[2];
        OutputStream outputStream = connection.getOutputStream();
        byte[] bytes = datos.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    private static String processServerResponse(HttpURLConnection connection) throws IOException {
        String response;
        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        inputStream.close();
        connection.disconnect();

        Document document = XMLConverter.convertStringToXMLDocument(stringBuilder.toString());
        response = XMLConverter.catchStatusResponseFromXMLDocument(document);

        return response;
    }

    static CompletableFuture<Document> consultUsers() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(DatabaseControler::createHTTPGetRequest, miExecutor);
    }

    private static Document createHTTPGetRequest() {
        Document document = null;
        try {
            HttpURLConnection connection = createHTTPConnection("http://192.168.1.113/consultausuarios2.php");
            document = readServerResponse(connection);
        } catch (IOException e) {
            Log.e(TAG, "Error in createHTTPGetRequest:\n\n\n" + e.getMessage());
        }
        return document;
    }

    private static Document readServerResponse(HttpURLConnection connection) throws IOException {
        Document document;
        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        inputStream.close();
        connection.disconnect();

        document = XMLConverter.convertStringToXMLDocument(stringBuilder.toString());

        return document;
    }

    private static final String TAG = "DatabaseControler";
}

