package org.bellator.wolf;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.json.JSONObject;

/**
 * Clase principal que representa una aplicación de escritorio para convertir monedas usando una API externa.
 * La aplicación permite al usuario seleccionar monedas, ingresar una cantidad, realizar la conversión y ver un historial.
 */
public class ConversorDeMonedas extends JFrame {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;
    private JTextArea historialArea;

    // Lista de monedas disponibles
    private final String[] currencies = {
        "USD", "EUR", "MXN", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "BRL",
        "ARS", "INR", "KRW", "RUB", "CLP", "COP", "PEN", "ZAR", "HKD", "SEK",
        "NOK", "DKK", "SGD", "PLN", "TRY", "THB", "IDR", "MYR", "NZD"
    };

    /**
     * Constructor principal que configura la interfaz gráfica del conversor de monedas.
     * Incluye selección de monedas, campo de cantidad, botón de conversión y área de historial.
     */
    public ConversorDeMonedas() {
        setTitle("Conversor de Monedas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500); // Tamaño adecuado para mostrar historial
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel con controles superiores (monedas, cantidad y botón)
        JPanel panelSuperior = new JPanel(new GridLayout(4, 2, 10, 10));
        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);
        amountField = new JTextField();
        JButton convertButton = new JButton("Convertir");
        resultLabel = new JLabel("Resultado: ");

        fromCurrency.setSelectedIndex(0);
        toCurrency.setSelectedIndex(1);

        panelSuperior.add(new JLabel("Moneda origen:"));
        panelSuperior.add(fromCurrency);
        panelSuperior.add(new JLabel("Moneda destino:"));
        panelSuperior.add(toCurrency);
        panelSuperior.add(new JLabel("Cantidad:"));
        panelSuperior.add(amountField);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(convertButton);

        // Área de historial de conversiones
        historialArea = new JTextArea(12, 40);
        historialArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historialArea);

        // Panel inferior con resultado e historial
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(resultLabel, BorderLayout.NORTH);
        panelInferior.add(new JLabel("Historial de conversiones:"), BorderLayout.CENTER);
        panelInferior.add(scrollPane, BorderLayout.SOUTH);

        // Añadir paneles al contenedor principal
        add(panelSuperior, BorderLayout.NORTH);
        add(panelInferior, BorderLayout.CENTER);

        // Eventos para evitar seleccionar la misma moneda
        fromCurrency.addActionListener(e -> verificarMonedas());
        toCurrency.addActionListener(e -> verificarMonedas());

        // Acción del botón de conversión
        convertButton.addActionListener(e -> convertir());

        // Cargar historial anterior desde archivo
        List<String> historial = HistorialConversiones.cargarHistorial();
        int desde = Math.max(0, historial.size() - 10);
        for (int i = desde; i < historial.size(); i++) {
            historialArea.append(historial.get(i) + "\n");
        }

        setVisible(true);
    }

    /**
     * Verifica que no se haya seleccionado la misma moneda de origen y destino.
     * Si ambas monedas son iguales, cambia automáticamente la moneda de destino.
     */
    private void verificarMonedas() {
        if (fromCurrency.getSelectedItem().equals(toCurrency.getSelectedItem())) {
            int selectedIndex = toCurrency.getSelectedIndex();
            toCurrency.setSelectedIndex((selectedIndex + 1) % currencies.length);
        }
    }

    /**
     * Realiza la conversión de moneda usando la API externa.
     * Muestra el resultado en la interfaz y lo guarda en el historial.
     */
    private void convertir() {
        String from = (String) fromCurrency.getSelectedItem();
        String to = (String) toCurrency.getSelectedItem();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);
            double rate = obtenerTasaDeCambio(from, to);
            double result = amount * rate;

            String resultadoTexto = String.format("Resultado: %.2f %s", result, to);
            resultLabel.setText(resultadoTexto);

            String log = String.format("%.2f %s = %.2f %s", amount, from, result, to);
            HistorialConversiones.guardarConversion(log);
            historialArea.append(log + "\n");

        } catch (NumberFormatException ex) {
            resultLabel.setText("Cantidad inválida.");
        } catch (Exception ex) {
            resultLabel.setText("Error al consultar tasas.");
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la tasa de cambio entre dos monedas desde la API de Frankfurter.
     *
     * @param from Moneda origen (por ejemplo, "USD").
     * @param to   Moneda destino (por ejemplo, "MXN").
     * @return La tasa de conversión como valor decimal.
     * @throws RuntimeException Si ocurre un error en la conexión o en la lectura de datos.
     */
    public static double obtenerTasaDeCambio(String from, String to) {
        try {
            String urlStr = "https://api.frankfurter.app/latest?amount=1&from=" + from + "&to=" + to;
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP Error: " + conn.getResponseCode());
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject json = new JSONObject(responseBuilder.toString());
                return json.getJSONObject("rates").getDouble(to);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo tasa: " + e.getMessage(), e);
        }
    }

    /**
     * Método principal que lanza la interfaz gráfica.
     *
     * @param args Argumentos de línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConversorDeMonedas::new);
    }
}
