package com.ejemplo.modelo;

public class Usuario {
    private final String usuario;
    private final String contraseña;

    public Usuario (String usuario, String contraseña) {

        this.usuario = usuario;
        this.contraseña = contraseña;

    }

    // El getter ahora se llama getUsuario() para que Thymeleaf lo reconozca
    public String getUsuario() { return usuario; } 
    public String getContraseña() { return contraseña;}
    // Si necesitas acceder a 'usuario' como 'nombre' en el futuro, podrías añadir
    // public String getNombre() { return usuario; }
}
