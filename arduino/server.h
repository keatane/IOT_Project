#include <Arduino.h>
#include "ArduinoHttpServer.h"
#include "ArduinoJson.h"
#include "WiFiS3.h"

namespace server {

class HTTPServer {
private:
  WiFiServer server;

public:
  HTTPServer(int port) : server(port) {}
  JsonDocument loop(JsonDocument (*callback)(JsonDocument)) {
    JsonDocument result;
    WiFiClient client = server.available();
    if (!client)
      return result;
    ArduinoHttpServer::StreamHttpRequest<512> httpRequest(client);
    bool success = httpRequest.readRequest();
    if (!success)
      return result;
    const char *body = httpRequest.getBody();
    deserializeJson(result, body);
    Serial.println("new client");
    JsonDocument response = callback(result);
    serializeJson(result, client);
    client.stop();
    Serial.println("client disconnected");
    return result;
  }
};
} // namespace server

using server::HTTPServer;
