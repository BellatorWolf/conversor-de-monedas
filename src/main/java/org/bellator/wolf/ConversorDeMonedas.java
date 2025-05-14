package org.bellator.wolf;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

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
 * También muestra un historial de las últimas conversiones realizadas.
 */
public class ConversorDeMonedas extends JFrame {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;

    private DefaultListModel<String> historyModel;
    private JList<String> historyList;

    private final String[] currencies = {
        "USD", "EUR", "MXN", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "BRL", 
        "ARS", "INR", "KRW", "RUB", "CLP", "COP", "PEN", "ZAR", "HKD", "SEK", 
        "NOK", "DKK", "SGD", "PLN", "TRY", "THB", "IDR", "MYR", "NZD"
    };

    /**
     * Constructor que inicializa la interfaz gráfica.
     */
    public ConversorDeMonedas() {
        setTitle("Conversor de Monedas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setSize(500, 350);
        setLocationRelativeTo(null);

        fromCurrency = new JComboBox<>(currencies);
        toCurrency = new JComboBox<>(currencies);
        amountField = new JTextField();
        ((AbstractDocument) amountField.getDocument()).setDocumentFilter(new DecimalFilter());
        JButton convertButton = new JButton("Convertir");
        resultLabel = new JLabel("Resultado: ");

        fromCurrency.setSelectedIndex(0);
        toCurrency.setSelectedIndex(1);

        // Historial
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        JScrollPane scrollPane = new JScrollPane(historyList);

        add(new JLabel("Moneda origen:"));
        add(fromCurrency);
        add(new JLabel("Moneda destino:"));
        add(toCurrency);
        add(new JLabel("Cantidad:"));
        add(amountField);
        add(new JLabel(""));
        add(convertButton);
        add(new JLabel(""));
        add(resultLabel);
        add(new JLabel("Historial:"));
        add(scrollPane);

        fromCurrency.addActionListener(e -> verificarMonedas());
        toCurrency.addActionListener(e -> verificarMonedas());
        convertButton.addActionListener(e -> convertir());

        setVisible(true);
    }

    /**
     * Evita que se seleccionen la misma moneda como origen y destino.
     */
    private void verificarMonedas() {
        if (fromCurrency.getSelectedItem().equals(toCurrency.getSelectedItem())) {
            int selectedIndex = toCurrency.getSelectedIndex();
            toCurrency.setSelectedIndex((selectedIndex + 1) % currencies.length);
        }
    }

    /**
     * Realiza la conversión y actualiza el historial.
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

            // Actualizar historial
            String entry = String.format("%.2f %s -> %.2f %s", amount, from, result, to);
            if (historyModel.size() >= 10) {
                historyModel.remove(0);
            }
            historyModel.addElement(entry);

        } catch (NumberFormatException ex) {
            resultLabel.setText("Cantidad inválida.");
        } catch (Exception ex) {
            resultLabel.setText("Error al consultar tasas.");
            ex.printStackTrace();
        }
    }

    /**
     * Llama a la API de Frankfurter para obtener la tasa de cambio entre dos monedas.
     */
    public static double obtenerTasaDeCambio(String from, String to) {
        try {
            String urlStr = "https://api.frankfurter.app/latest?amount=1&from=" + from + "&to=" + to;
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Error en la conexión HTTP: Código " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject json = new JSONObject(responseBuilder.toString());

                if (!json.has("rates") || json.isNull("rates")) {
                    throw new RuntimeException("No se encontró la tasa en la respuesta.");
                }

                return json.getJSONObject("rates").getDouble(to);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la tasa de cambio: " + e.getMessage(), e);
        }
    }

    /**
     * Método principal para iniciar la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConversorDeMonedas::new);
    }
}
