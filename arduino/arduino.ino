#include "MQTT.h"
#include "WiFiS3.h"

const char HOTSPOT_SSID[] = "caraffa_1";
WiFiServer server(80);
const IPAddress SERVER_ADDRESS = IPAddress(192, 168, 4, 1);
const int ID = 1;
String ssid;
String pw;
bool paired;

volatile int spins;
const double C=1.0/(60*10); // litres/spin
const int FLOW_SENSOR = 2;

const char MQTT_BROKER[] = "212.78.1.205";
const int MQTT_PORT = 1883;
const char MQTT_USERNAME[] = "studenti";
const char MQTT_PASSWORD[] = "studentiDRUIDLAB_1";
const char MQTT_ID[]="jug1";
const char MQTT_SENSOR_TOPIC[]="/Thingworx/Jug1/litresPerSecond";
const char MQTT_TOKEN_TOPIC[]="/jug/pair";

WiFiClient wifiMqttClient;
MQTTClient mqttClient;


void rpm() {
  spins++;
}

void exit() {
  while (true)
    ;
}

void setup() {
  Serial.begin(9600);
  while (!Serial)
    ;
  Serial.println("Access Point Web Server");
  if (WiFi.status() == WL_NO_MODULE) {
    Serial.println("Communication with WiFi module failed!");
    exit();
  }
  String fv = WiFi.firmwareVersion();
  if (fv < WIFI_FIRMWARE_LATEST_VERSION) {
    Serial.println("Please upgrade the firmware");
  }
  attachInterrupt(digitalPinToInterrupt(FLOW_SENSOR), rpm, RISING); // Attach interrupt
  setUnpaired();
}

void loop() {
  if (paired)
    loopPaired();
  else
    loopUnpaired();
}

void setUnpaired() {
  paired = false;
  WiFi.config(SERVER_ADDRESS);

  Serial.print("Creating access point named: ");
  Serial.println(HOTSPOT_SSID);

  int status = WiFi.beginAP(HOTSPOT_SSID);
  if (status != WL_AP_LISTENING) {
    Serial.println("Creating access point failed");
    exit();
  }

  delay(10000);

  server.begin();

  printWiFiStatus();
}

void loopUnpaired() {
  WiFiClient client = server.available();
  if (client) {
    Serial.println("new client");
    String content = "";
    while (client.connected()) {
      delayMicroseconds(10);
      if (client.available()) {
        char c = client.read();
        Serial.write(c);
        content += c;
      }
    }
    pair(content);
    client.println("OK");
    client.stop();
    Serial.println("client disconnected");
  }
}

void pair(String content) {
  const int LENGTH = 3;
  String pieces[LENGTH];
  splitString(content, '\n', pieces, LENGTH);
  String ssid = pieces[0];
  String pw = pieces[1];
  String token = pieces[2];
  Serial.print("SSID: ");
  Serial.println(ssid);
  Serial.print("PW: ");
  Serial.println(pw);
  Serial.print("TOKEN: ");
  Serial.println(token);
  setPaired(ssid, pw, token);
}

void splitString(String text, char split, String *output, int length) {
  for (int i = 0; i < length; ++i) {
    int index = text.indexOf(split);
    if (index == -1) {
      output[i] = "";
      continue;
    }
    output[i] = text.substring(0, index);
    text = text.substring(index + 1);
  }
}

void setPaired(String in_ssid, String in_pw, String token) {
  ssid = in_ssid;
  pw = in_pw;
  paired = true;
  const IPAddress EMPTY = IPAddress(0, 0, 0, 0);
  WiFi.config(EMPTY, EMPTY, EMPTY);
  connect();
  publishMQTT(MQTT_TOKEN_TOPIC,token);
  // subscribe();
}

void connect() {
  while (WiFi.status() != WL_CONNECTED) {
    int status = WiFi.begin(ssid.c_str(), pw.c_str());
    if (status == WL_CONNECTED) {
      IPAddress ip = WiFi.localIP();
      Serial.print("IP Address: ");
      Serial.println(ip);
      connectMQTT();
    }
    delay(1000);
  }
}

void connectMQTT() {
  mqttClient.begin(MQTT_BROKER, MQTT_PORT, wifiMqttClient);
  while (!mqttClient.connect(MQTT_ID, MQTT_USERNAME, MQTT_PASSWORD)) {
    Serial.println(".");
    delay(1000);
  }
}

void publishMQTT(String topic,String message) { mqttClient.publish(topic,message); }

void subscribeMQTT() {}

void loopPaired() {
  connect();
  Serial.println("CONNECTED and looping");
  spins=0;
  delay(1000);
  double litres=spins*C;
  Serial.println(litres);
  Serial.println(String(litres));
  publishMQTT(MQTT_SENSOR_TOPIC,String(litres).c_str());
}

void printWiFiStatus() {
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.print("To see this page in action, open a browser to http://");
  Serial.println(ip);
}
