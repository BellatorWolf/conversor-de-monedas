# Conversor de Monedas

Aplicación de escritorio en Java que permite convertir montos entre diferentes monedas utilizando una interfaz gráfica (GUI) desarrollada con **Swing**.

## 🧩 Características

- Interfaz gráfica intuitiva para seleccionar monedas y montos.
- Consulta en tiempo real de tasas de cambio desde la API pública de [Frankfurter](https://www.frankfurter.app/).
- Validación de entrada para evitar conversiones inválidas.
- Prevención de selección de monedas repetidas (misma moneda origen y destino).
- Se puede ver un historial de conversiones anteriores.

## 🖥️ Captura de pantalla
![image](https://github.com/user-attachments/assets/897797b6-5cd2-429f-9da1-8ae6e5844f20)




## 🚀 Requisitos

- Java 21 o superior
- Maven 3.x
- Conexión a Internet

## Créditos

- API de tasas: [Frankfurter.app](https://www.frankfurter.app/)

## 🔧 Cómo ejecutar
```bash
  mvn clean package
  java -jar target/ConversorDeMonedas-0.0.1-SNAPSHOT.jar
