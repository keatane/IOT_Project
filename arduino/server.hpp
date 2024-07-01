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
  bool loop(bool (*callback)(const JsonDocument &)) {
    JsonDocument result;
    WiFiClient client = server.available();
    if (!client)
      return false;
    ArduinoHttpServer::StreamHttpRequest<512> httpRequest(client);
    bool success = httpRequest.readRequest();
    if (!success)
      return false;
    const char *body = httpRequest.getBody();
    Serial.print("Body: ");
    Serial.println(body);
    deserializeJson(result, body);
    Serial.println("new client");
    Serial.print("json:");
    serializeJson(result, Serial);
    Serial.println("");
    bool ok=callback(result);
    ArduinoHttpServer::StreamHttpReply httpReply(client,"application/json");
    httpReply.send(ok?"{}":"{\"error\":\"Malformed request\"}");
    Serial.println("closing client");
    client.flush();
    client.stop();
    delay(1000);
    Serial.println("client disconnected");
    return ok;
  }
};
