#include "ArduinoHttpServer.h"
#include "ArduinoJson.h"
#include "WiFiS3.h"
#include <Arduino.h>

class HTTPServer {
private:
  WiFiServer server;

public:
  HTTPServer(int port) : server(port) {}
  void begin() { server.begin(); }
  JsonDocument loop(void (*callback)(JsonDocument &)) {
    JsonDocument result;
    WiFiClient client = server.available();
    if (!client)
      return result;
    ArduinoHttpServer::StreamHttpRequest<512> httpRequest(client);
    bool success = httpRequest.readRequest();
    if (!success)
      return result;
    const char *body = httpRequest.getBody();
    Serial.print("Body: ");
    Serial.println(body);
    deserializeJson(result, body);
    Serial.println("new client");
    Serial.print("json:");
    serializeJson(result, Serial);
    Serial.println("");
    callback(result);
    serializeJson(result, client);
    client.stop();
    Serial.println("client disconnected");
    return result;
  }
};
