#include <Arduino.h>
#include <WiFiS3.h>

class Wifi {
private:
  String ssid;
  String pw;

public:
  void check() {
    Serial.println("Access Point Web Server");
    if (WiFi.status() == WL_NO_MODULE) {
      Serial.println("Communication with WiFi module failed!");
      exit();
    }
    String fv = WiFi.firmwareVersion();
    if (fv < WIFI_FIRMWARE_LATEST_VERSION) {
      Serial.println("Please upgrade the firmware");
    }
  }
  void createAP(IPAddress server_address, String ssid) {
    WiFi.config(server_address);

    Serial.print("Creating access point named: ");
    Serial.println(ssid);

    int status = WiFi.beginAP(ssid.c_str());
    if (status != WL_AP_LISTENING) {
      Serial.println("Creating access point failed");
      exit();
    }

    delay(10000);

    // Print wifi status
    Serial.print("SSID: ");
    Serial.println(WiFi.SSID());
    IPAddress ip = WiFi.localIP();
    Serial.print("IP Address: ");
    Serial.println(ip);
    Serial.print("To see this page in action, open a browser to http://");
    Serial.println(ip);
  }
  void connect(String ssid, String pw) {
    this->ssid = ssid;
    this->pw = pw;
    ensureConnected();
  }
  bool ensureConnected() {
    while (WiFi.status() != WL_CONNECTED) {
      const IPAddress EMPTY = IPAddress(0, 0, 0, 0);
      WiFi.config(EMPTY, EMPTY, EMPTY);
      int status = WiFi.begin(ssid.c_str(), pw.c_str());
      if (status == WL_CONNECTED) {
        IPAddress ip = WiFi.localIP();
        Serial.print("IP Address: ");
        Serial.println(ip);
        return true;
      }
      delay(1000);
    }
    return false;
  }
};
