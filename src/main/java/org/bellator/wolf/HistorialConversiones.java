package org.bellator.wolf;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialConversiones {
    private static final String ARCHIVO_HISTORIAL = "historial.txt";

    public static void guardarConversion(String entrada) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_HISTORIAL, true))) {
            writer.write(entrada);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar el historial: " + e.getMessage());
        }
    }

    public static List<String> cargarHistorial() {
        List<String> historial = new ArrayList<>();
        File archivo = new File(ARCHIVO_HISTORIAL);
        if (!archivo.exists()) return historial;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                historial.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el historial: " + e.getMessage());
        }

        return historial;
    }
}
