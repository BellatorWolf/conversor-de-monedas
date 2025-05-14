# Conversor de Monedas

Aplicación de escritorio en Java que permite convertir montos entre diferentes monedas utilizando una interfaz gráfica (GUI) desarrollada con **Swing**.

## 🧩 Características

- Interfaz gráfica intuitiva para seleccionar monedas y montos.
- Consulta en tiempo real de tasas de cambio desde la API pública de [Frankfurter](https://www.frankfurter.app/).
- Validación de entrada para evitar conversiones inválidas.
- Prevención de selección de monedas repetidas (misma moneda origen y destino).

## 🖥️ Captura de pantalla
![image](https://github.com/user-attachments/assets/a8b190a3-4b52-4b40-bd68-e9987ef0a64a)



## 🚀 Requisitos

- Java 17 o superior
- Maven 3.x
- Conexión a Internet

## 🔧 Instalación y ejecución

1. **Clona este repositorio:**

   ```bash
   git clone https://github.com/tu-usuario/ConversorDeMonedas.git
   cd ConversorDeMonedas
   

## Créditos

- API de tasas: [Frankfurter.app](https://www.frankfurter.app/)

## Cómo ejecutar
```bash
  mvn clean package
  java -jar target/ConversorDeMonedas-0.0.1-SNAPSHOT.jar
