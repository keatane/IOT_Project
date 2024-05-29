#include <Arduino.h>
#include "server.hpp"
#include "utils.hpp"
#include "water.hpp"
#include "mqtt.hpp"
#include "wifi.hpp"
#include "consts.hpp"
#include "secrets.hpp"

struct PairingData{
    String ssid;
    String pw;
    String token;
};

enum State { UNPAIRED, PAIRING,PAIRED };
State state = UNPAIRED;
PairingData userData;

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
  waterSensor.begin();
}

void loop() {
  switch (state) {
  case UNPAIRED:
    loopUnpaired();
    break;
  case PAIRING:
    loopPairing();
    break;
  case PAIRED:
    loopPaired();
    break;
  }
}

void setUnpaired() {
  state=UNPAIRED;
  wifi.createAP(SERVER_ADDRESS, HOTSPOT_SSID);
  httpServer.begin();
}

void loopPairing(){
  wifi.connect(userData.ssid, userData.pw);
  wifi.ensureConnected();
  if(!mqtt.connect()){
      setUnpaired();
      return;
  }
  JsonDocument request;
  request["token"]=userData.token;
  request["id"]=JUG_ID;
  mqtt.publishJson(MQTT_TOKEN_TOPIC, request);
  JsonDocument response;
  mqtt.recvJson(response);
  if(response["status"]!="OK"){
      setUnpaired();
      return;
  }
  setPaired();
}

void loopUnpaired() { httpServer.loop(pair); }

void pair(JsonDocument& document) {
  serializeJson(document, Serial);
  if (!document.containsKey("ssid") || !document.containsKey("pw") ||
      !document.containsKey("token")) {
    document.clear();
    document["status"] = "Malformed";
    return;
  }
  setPairing(document["ssid"],document["pw"],document["token"]);
  document.clear();
  document["status"] = "OK";
}

void setPairing(const String& ssid,const String& pw,const String& token){
    userData.ssid=ssid;
    userData.pw=pw;
    userData.token=token;
    state=PAIRING;
}

void setPaired() {
  state=PAIRED;
}

void loopPaired() {
  if(wifi.ensureConnected())mqtt.connect();
  Serial.println("CONNECTED and looping");
  double litres = waterSensor.measure();
  Serial.println(litres);
  Serial.println(String(litres));
  mqtt.publish(MQTT_SENSOR_TOPIC, String(litres).c_str());
}
