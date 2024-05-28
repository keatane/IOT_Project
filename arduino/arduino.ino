#include <Arduino.h>
#include "server.h"
#include "utils.h"
#include "water.h"
#include "mqtt.h"
#include "wifi.h"
#include "consts.h"
#include "secrets.h"

enum State { UNPAIRED, PAIRED };
State state = UNPAIRED;

WaterSensor waterSensor(FLOW_SENSOR);
HTTPServer httpServer(SERVER_PORT);
Wifi wifi;
MQTT mqtt(MQTT_BROKER,MQTT_PORT,MQTT_USERNAME,MQTT_PASSWORD,MQTT_ID);

void setup() {
  Serial.begin(9600);
  while (!Serial)
    ;
  wifi.check();
  setUnpaired();
}

void loop() {
  switch (state) {
  case UNPAIRED:
    loopUnpaired();
    break;
  case PAIRED:
    loopPaired();
    break;
  }
}

void setUnpaired() {
  state=UNPAIRED;
  wifi.createAP(SERVER_ADDRESS, HOTSPOT_SSID);
}

void loopUnpaired() { httpServer.loop(pair); }

JsonDocument pair(JsonDocument document) {
  JsonDocument result;
  JsonVariant variant = document.to<JsonVariant>();
  serializeJsonPretty(document, Serial);
  if (!document.containsKey("ssid") || !document.containsKey("pw") ||
      !document.containsKey("token")) {
    result["status"] = "Malformed";
    return result;
  }
  result["status"] = "OK";
  return result;
}

void setPaired(String ssid, String pw, String token) {
  state=PAIRED;
  wifi.connect(ssid, pw);
  mqtt.connect();
  mqtt.publish(MQTT_TOKEN_TOPIC, token);
  // subscribe();
}

void loopPaired() {
  if(wifi.ensureConnected())mqtt.connect();
  Serial.println("CONNECTED and looping");
  double litres = waterSensor.measure();
  Serial.println(litres);
  Serial.println(String(litres));
  mqtt.publish(MQTT_SENSOR_TOPIC, String(litres).c_str());
}
