#include <WiFi.h>
#include "DHT.h"

const char* ssid = "M11sftd";
const char* password = "ABCD";

#define DHTPIN 4
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

WiFiServer server(80);

void setup() {
  // Serial.begin(9600);
  pinMode(14, OUTPUT);
  pinMode(12, OUTPUT);
  Serial.println(F("DHTxx test!"));

  Serial.begin(115200);
  dht.begin();
  WiFi.begin(ssid, password);

  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("Connected!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  server.begin();
}

void loop() {
  WiFiClient client = server.available();
  if (client) {
    Serial.println("New Client Connected.");
    String request = client.readStringUntil('\r');
    client.flush();

    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();

    if (isnan(temperature) || isnan(humidity)) {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }

    if (temperature < 38) {
      digitalWrite(12, HIGH);
      delay(1000);
      digitalWrite(12, LOW);
      delay(1000);
    }

    if (temperature > 59) {
      digitalWrite(14, HIGH);
      delay(1000);
      digitalWrite(14, LOW);
      delay(1000);
    }

    String html = "<!DOCTYPE html><html><head><title>Sensor Data</title></head><body>";
    html += "<h1>Temperature and Humidity</h1>";
    html += "<p>Temperature: " + String(temperature) + " °C</p>";
    html += "<p>Humidity: " + String(humidity) + " %</p>";
    html += "</body></html>";

    client.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
    client.print(html);
    delay(1);
    client.stop();
    Serial.println("Client Disconnected.");
  }
}
