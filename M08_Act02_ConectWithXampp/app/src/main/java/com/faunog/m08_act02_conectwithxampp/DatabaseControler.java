package com.faunog.m08_act02_conectwithxampp;

import android.os.StrictMode;
import android.util.Log;

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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DatabaseControler {

    static CompletableFuture<String> validateUser(String username, String password) {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            String response = null;
            String url = "http://192.168.1.113/validacuenta2.php";

            try {
                // Crear la conexion HTTP
                URL direction = new URL(url);
                HttpURLConnection connexion = (HttpURLConnection) direction.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoOutput(true);

                // Crear los datos del formulario
                String datos = "usuario=" + username + "&contrasena=" + password;

                // Escribir los datos del formulario en la solicitud HTTP
                OutputStream outputStream = connexion.getOutputStream();
                byte[] bytes = datos.getBytes(StandardCharsets.UTF_8);
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();

                // Leer la respuesta del servidor
                InputStream inputStream = connexion.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // Cerrar la conexión HTTP
                inputStream.close();
                connexion.disconnect();


                // Procesar la respuesta del servidor
                response = stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }, miExecutor);
    }

    static CompletableFuture<Boolean> isServerRunning() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = "http://192.168.1.113";
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                final int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }, miExecutor);
    }

    static CompletableFuture<Boolean> isMySQLRunning(String user, String password) {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return connectDatabase(user, password).isValid(100);
            } catch (SQLException e) {
                System.err.println("FUE ESO:\n\n\n\n\n\n" + e.getErrorCode());
                return false;
            }
        }, miExecutor);
    }

    private static Connection connectDatabase(String user, String password) {
        final String urlJDBC = "jdbc:mysql://192.168.1.113:3307/m08_act02";
        final String class_jdbc = "com.mysql.jdbc.Driver";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName(class_jdbc);
            conn = DriverManager.getConnection(urlJDBC, user, password);
        } catch (Exception e) {
            Log.e(TAG, "Error in databaseNotAvailable:\n\n\n" + Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }

    private static final String TAG = "DatabaseControler";
}
