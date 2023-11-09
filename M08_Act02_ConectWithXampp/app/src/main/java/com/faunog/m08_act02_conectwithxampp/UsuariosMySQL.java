package com.faunog.m08_act02_conectwithxampp;

/**
 * Clase de registro que representa la información de un usuario en una base de datos MySQL.
 * Esta clase almacena el nombre de usuario, la contraseña y la fecha de nacimiento del usuario.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public record UsuariosMySQL(String usuario, String contrasena, String fecha_nacimiento) {
}
