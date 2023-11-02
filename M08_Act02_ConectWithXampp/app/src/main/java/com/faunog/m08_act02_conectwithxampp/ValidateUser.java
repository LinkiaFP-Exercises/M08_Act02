package com.faunog.m08_act02_conectwithxampp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ValidateUser {

    public static CompletableFuture<String> validateAsync(String username, String password) {
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
}

