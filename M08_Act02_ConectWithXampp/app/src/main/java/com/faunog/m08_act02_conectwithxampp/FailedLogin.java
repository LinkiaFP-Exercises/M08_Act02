package com.faunog.m08_act02_conectwithxampp;

/**
 * Clase de registro que representa un intento de inicio de sesión fallido.
 * Esta clase almacena el ID del intento de inicio de sesión, el nombre de usuario, la contraseña y la fecha y hora del intento.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public record FailedLogin(int id, String username, String password, String dateTime) {
}
