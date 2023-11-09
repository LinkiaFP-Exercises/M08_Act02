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


/**
 * La clase DatabaseControler proporciona métodos para controlar la interacción con la base de datos remota y local.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class DatabaseControler {

    /**
     * Realiza una comprobación para determinar si el servidor está en funcionamiento.
     *
     * @return Un CompletableFuture que indica si el servidor está en funcionamiento.
     */
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

    /**
     * Realiza una comprobación para determinar si MySQL está en funcionamiento.
     *
     * @param user     El nombre de usuario.
     * @param password La contraseña.
     * @return Un CompletableFuture que indica si MySQL está en funcionamiento.
     */
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

    /**
     * Establece una conexión con la base de datos mediante el controlador JDBC de MySQL.
     *
     * @param user     El nombre de usuario para la conexión.
     * @param password La contraseña para la conexión.
     * @return Una conexión a la base de datos si la conexión es exitosa, de lo contrario, lanza una excepción.
     * @throws SQLException           si ocurre un error al intentar conectarse a la base de datos.
     * @throws ClassNotFoundException si no se puede encontrar la clase del controlador JDBC de MySQL.
     */
    private static Connection connectDatabase(String user, String password) throws SQLException, ClassNotFoundException {
        final String urlJDBC = "jdbc:mysql://192.168.1.113:3307/m08_act02";
        final String classJDBC = "com.mysql.jdbc.Driver";
        Connection conn;

        Class.forName(classJDBC);
        conn = DriverManager.getConnection(urlJDBC, user, password);

        return conn;
    }


    /**
     * Valida un usuario mediante el envío de datos al servidor.
     *
     * @param statusUserPass Un arreglo que contiene el estado, el nombre de usuario y la contraseña.
     * @return Un CompletableFuture con la respuesta del servidor.
     * @see XMLConverter#convertStringToXMLDocument(String)
     * @see XMLConverter#catchStatusResponseFromXMLDocument(Document)
     */
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

    /**
     * Crea una conexión HTTP y configura los parámetros necesarios para una solicitud POST.
     *
     * @param url La URL a la que se va a conectar.
     * @return Una conexión HTTP configurada para realizar una solicitud POST.
     * @throws IOException si ocurre un error de entrada/salida al abrir la conexión.
     */
    private static HttpURLConnection createHTTPConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Envía datos al servidor a través de la conexión HTTP proporcionada.
     *
     * @param connection     La conexión HTTP a través de la cual se enviarán los datos.
     * @param statusUserPass Los datos de usuario y contraseña para enviar al servidor.
     * @throws IOException si ocurre un error de entrada/salida al enviar los datos al servidor.
     */
    private static void sendDataToServer(HttpURLConnection connection, String[] statusUserPass) throws IOException {
        String datos = "usuario=" + statusUserPass[1] + "&contrasena=" + statusUserPass[2];
        OutputStream outputStream = connection.getOutputStream();
        byte[] bytes = datos.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Procesa la respuesta del servidor, convirtiéndola a un documento XML y capturando la respuesta de estado.
     *
     * @param connection La conexión HTTP de la que se obtiene la respuesta del servidor.
     * @return La respuesta del servidor en forma de cadena de texto.
     * @throws IOException si ocurre un error de entrada/salida al procesar la respuesta del servidor.
     */
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

    /**
     * Consulta los usuarios en la base de datos remota.
     *
     * @return Un CompletableFuture con el documento XML resultante.
     * @see XMLConverter#convertStringToXMLDocument(String)
     */
    static CompletableFuture<Document> consultUsers() {
        Executor miExecutor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(DatabaseControler::createHTTPGetRequest, miExecutor);
    }

    /**
     * Crea una solicitud HTTP GET y obtiene el documento XML de la respuesta del servidor.
     *
     * @return El documento XML de la respuesta del servidor.
     */
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

    /**
     * Lee la respuesta del servidor a través de la conexión HTTP y la convierte en un documento XML.
     *
     * @param connection La conexión HTTP de la que se obtiene la respuesta del servidor.
     * @return El documento XML que representa la respuesta del servidor.
     * @throws IOException si ocurre un error de entrada/salida al leer la respuesta del servidor.
     */
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

