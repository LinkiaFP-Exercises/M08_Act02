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

/*
    public String[] validate(String user, String password) {
        String[] response = new String[1];
        Executor miExecutor = Executors.newSingleThreadExecutor();
        miExecutor.execute(() -> {
            String url = "http://192.168.1.113/validacuenta2.php"; // Reemplaza esto con la URL de tu archivo PHP
            // Resto de tu código existente aquí...

            try {
                // Crear la connexion HTTP
                URL direction = new URL(url);
                HttpURLConnection connexion = (HttpURLConnection) direction.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoOutput(true);

                // Crear los datos del formulario
                String datos = "usuario=" + user + "&contrasena=" + password;

                // Escribir los datos del formulario en la solicitud HTTP
                OutputStream outputStream = connexion.getOutputStream();
                byte[] bytes = datos.getBytes(StandardCharsets.UTF_8);
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();

                // Leer la respuesta del servidor
                InputStream entrada = connexion.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder stringBuilder = new StringBuilder();
                String linea;

                while ((linea = bufferedReader.readLine()) != null) {
                    stringBuilder.append(linea);
                }

                // Cerrar la conexión HTTP
                entrada.close();
                connexion.disconnect();

                // Procesar la respuesta del servidor
                response[0] = stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Esperar hasta que se complete la ejecución del hilo secundario
        while (response[0] == null) {
            try {
                Thread.sleep(100); // Puedes ajustar el tiempo de espera según sea necesario
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
*/
}

