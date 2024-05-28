#include <Arduino.h>
#include <MQTT.h>

class MQTT {
private:
  WiFiClient wifiClient;
  MQTTClient mqttClient;
  String username;
  String password;
  String id;

public:
  MQTT(String broker, int port, String username, String password, String id)
      : username(username), password(password), id(id) {
    mqttClient.begin(broker.c_str(), port, wifiClient);
  }
  void connect() {
    while (!mqttClient.connect(id.c_str(), username.c_str(), password)) {
      Serial.println(".");
      delay(1000);
    }
  }
  void publish(String topic,String message) { mqttClient.publish(topic, message); }
};
