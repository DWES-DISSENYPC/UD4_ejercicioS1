package com.ejemplo.services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class GestionFicherosService {

    // d) Constante para el tamaño máximo (2 MB)
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    // a) Obtiene y muestra el nombre del fichero subido
    private static String getNombreFichero(Part fichero) {
        // Maneja la obtención segura del nombre del fichero
        String nombre = Paths.get(fichero.getSubmittedFileName()).getFileName().toString();
        System.out.println("Fichero subido detectado: " + nombre);
        return nombre;
    }

    // b) Valida la extensión del fichero
    private static boolean validarExtension(String nombreFichero, String extensionEsperada) {
        if (nombreFichero == null || extensionEsperada == null) {
            return false;
        }
        boolean esValida = nombreFichero.toLowerCase().endsWith(extensionEsperada.toLowerCase());
        if (!esValida) {
            System.err.println("Validación Fallida: Extensión no válida. Se esperaba: " + extensionEsperada);
        }
        return esValida;
    }

    // d) Comprobación del tamaño máximo del fichero
    private static boolean validarTamanyo(Part fichero) {
        System.out.println("Entrando en validarTamanyo()...");
        long tamanyo = fichero.getSize();
        System.out.println("Tamaño detectado: " + tamanyo);
        boolean esValido = tamanyo <= MAX_FILE_SIZE;
        if (!esValido) {
            System.err.println("Validación Fallida: El fichero excede el tamaño máximo ("
                    + MAX_FILE_SIZE / (1024 * 1024) + " MB). Tamaño: " + tamanyo + " bytes.");
        }
        return esValido;
    }

    // f) Comprueba si el fichero ya existe en el directorio de destino
    private static boolean ficheroYaExiste(Path rutaDestino) {
        boolean existe = Files.exists(rutaDestino);
        if (existe) {
            System.err.println("Validación Fallida: El fichero de destino ya existe en la ruta.");
        }
        return existe;
    }

  
    public static boolean validarFichero(HttpServletRequest req, String extensionEsperada) {

        String nombreUsuario = req.getParameter("usuario");

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.err.println("Error: El parámetro 'usuario' es nulo o vacío.");
            return false;
        }

        try {
            Part fichero = req.getPart("fichero");

            // a) Obtener el nombre del fichero para su uso y log
            String nombreFicheroOriginal = getNombreFichero(fichero);

            // 2. Validar Tamaño Máximo (d)
            if (!validarTamanyo(fichero)) {
                return false;
            }

            // 1. Validar Extensión (b)
            if (!validarExtension(nombreFicheroOriginal, extensionEsperada)) {
                return false;
            }

           

            // --- Rutas y Nombre de Destino ---

            // Importante: Cambia 'E:\\web_file' por la letra de tu unidad si es necesario.
            Path directorioBase = Paths.get("E:", "web_file");
            Path directorioUsuario = directorioBase.resolve(nombreUsuario);

            // Determinar el nombre final (nombre_mayus.txt)
            int posPunto = nombreFicheroOriginal.lastIndexOf(".");
            String nombreBase = nombreFicheroOriginal.substring(0, posPunto);
            String extension = nombreFicheroOriginal.substring(posPunto);
            String nombreFinalFichero = nombreBase + "_mayus" + extension;
            Path rutaFinal = directorioUsuario.resolve(nombreFinalFichero);

            // 3. Validar Existencia (f)
            if (ficheroYaExiste(rutaFinal)) {
                return false;
            }

            // --- Procesamiento y Guardado (Solo si todas las validaciones OK) ---

            Files.createDirectory(directorioUsuario); // Crea la carpeta si no existe

            // Uso de try-with-resources (crucial para I/O segura) y Files.newBufferedWriter
            // (NIO.2)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fichero.getInputStream()));
                    BufferedWriter bw = Files.newBufferedWriter(rutaFinal, StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE)) {

                String linea;
                while ((linea = br.readLine()) != null) {
                    bw.write(linea.toUpperCase());
                    bw.newLine();
                }

                System.out.println("Procesamiento y guardado OK en: " + rutaFinal);
            }

        } catch (IOException e) {
            System.out.println("Error de I/O, permisos o acceso a directorios: " + e.getMessage());
            e.printStackTrace();
            return false;

        } catch (ServletException e) {
            System.out.println("Error al obtener la parte del fichero subido (ServletException): " + e.getMessage());
            return false;
        }

        return true;
    }
}