package org.bellator.wolf;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

/**
 * Clase que implementa una interfaz gráfica de usuario (GUI) para convertir
 * monedas usando una API externa para obtener las tasas de cambio.
 */
public class ConversorDeMonedas extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Componentes de la interfaz gráfica
    private JComboBox<String> fromCurrency; // Combo box para seleccionar la moneda de origen
    private JComboBox<String> toCurrency;   // Combo box para seleccionar la moneda de destino
    private JTextField amountField;         // Campo de texto para ingresar la cantidad
    private JLabel resultLabel;             // Etiqueta para mostrar el resultado de la conversión

    // Lista de monedas disponibles
    private final String[] currencies = {"USD", "EUR", "MXN", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "BRL", 
                                         "ARS", "INR", "KRW", "RUB", "CLP", "COP", "PEN", "ZAR", "HKD", "SEK", 
                                         "NOK", "DKK", "SGD", "PLN", "TRY", "THB", "IDR", "MYR", "NZD"};

    /**
     * Constructor que inicializa la interfaz gráfica.
     * Configura la ventana, los componentes y sus acciones.
     */
    public ConversorDeMonedas() {
        setTitle("Conversor de Monedas");  // Título de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Acción al cerrar la ventana
        setLayout(new GridLayout(5, 2, 10, 10));  // Layout de la ventana
        setSize(400, 250);  // Tamaño de la ventana
        setLocationRelativeTo(null);  // Centra la ventana en la pantalla

        // Inicialización de los componentes
        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);
        amountField = new JTextField();
        JButton convertButton = new JButton("Convertir");
        resultLabel = new JLabel("Resultado: ");

        // Inicializamos las monedas con valores predeterminados diferentes
        fromCurrency.setSelectedIndex(0); // Moneda de origen seleccionada por defecto
        toCurrency.setSelectedIndex(1);   // Moneda de destino seleccionada por defecto

        // Agregar los componentes a la ventana
        add(new JLabel("Moneda origen:"));
        add(fromCurrency);
        add(new JLabel("Moneda destino:"));
        add(toCurrency);
        add(new JLabel("Cantidad:"));
        add(amountField);
        add(new JLabel(""));  // Espacio vacío
        add(convertButton);
        add(new JLabel(""));  // Espacio vacío
        add(resultLabel);

        // Acción para evitar que las mismas monedas sean seleccionadas
        fromCurrency.addActionListener(e -> verificarMonedas());
        toCurrency.addActionListener(e -> verificarMonedas());

        // Acción para convertir la cantidad al presionar el botón
        convertButton.addActionListener(e -> convertir());

        setVisible(true);  // Mostrar la ventana
    }

    /**
     * Verifica si las monedas seleccionadas son iguales.
     * Si son iguales, cambia automáticamente la moneda de destino a la siguiente en la lista.
     */
    private void verificarMonedas() {
        // Si las monedas de origen y destino son iguales, cambiamos la moneda destino
        if (fromCurrency.getSelectedItem().equals(toCurrency.getSelectedItem())) {
            int selectedIndex = toCurrency.getSelectedIndex();
            toCurrency.setSelectedIndex((selectedIndex + 1) % currencies.length); // Cambiar a la siguiente moneda
        }
    }

    /**
     * Convierte la cantidad ingresada utilizando la tasa de cambio obtenida de la API externa.
     */
    private void convertir() {
        // Obtenemos las monedas seleccionadas y la cantidad ingresada
        String from = (String) fromCurrency.getSelectedItem();
        String to = (String) toCurrency.getSelectedItem();
        String amountText = amountField.getText();

        try {
            // Intentamos convertir la cantidad ingresada a un número
            double amount = Double.parseDouble(amountText);
            // Obtenemos la tasa de cambio entre las monedas
            double rate = obtenerTasaDeCambio(from, to);
            // Calculamos el resultado
            double result = amount * rate;
            resultLabel.setText("Resultado: " + result + " " + to);  // Mostramos el resultado
        } catch (NumberFormatException ex) {
            // Si la cantidad ingresada no es válida, mostramos un mensaje de error
            resultLabel.setText("Cantidad inválida.");
        } catch (Exception ex) {
            // Si ocurre un error al consultar la tasa de cambio, mostramos un mensaje de error
            resultLabel.setText("Error al consultar tasas.");
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la tasa de cambio entre dos monedas utilizando la API de Frankfurter.
     * 
     * @param from La moneda de origen
     * @param to La moneda de destino
     * @return La tasa de cambio entre las dos monedas
     */
    public static double obtenerTasaDeCambio(String from, String to) {
        try {
            // Construimos la URL para la API
            String urlStr = "https://api.frankfurter.app/latest?amount=100&from=" + from + "&to=" + to;
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            // Abrimos una conexión HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Obtenemos el código de respuesta de la conexión
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Error en la conexión HTTP: Código " + responseCode);
            }

            // Leemos la respuesta de la API
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                // Parseamos la respuesta JSON
                JSONObject json = new JSONObject(responseBuilder.toString());

                // Verificamos que la respuesta contenga la tasa de cambio
                if (!json.has("rates") || json.isNull("rates")) {
                    throw new RuntimeException("No se encontró la tasa en la respuesta.");
                }

                // Extraemos la tasa de cambio de la moneda
                return json.getJSONObject("rates").getDouble(to);
            }

        } catch (Exception e) {
            // Si ocurre un error, lanzamos una excepción
            throw new RuntimeException("Error al obtener la tasa de cambio: " + e.getMessage(), e);
        }
    }

    /**
     * Método principal para iniciar la aplicación.
     * Llama al constructor de la clase y establece la interfaz gráfica.
     */
    public static void main(String[] args) {
        // Inicia la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(ConversorDeMonedas::new);
    }
}
