#include <Arduino.h>
#include <MQTT.h>
#include "ArduinoJson.h"

const char* getMQTTError(int code){
    switch(code){
        case -1:
            return "Buffer too short";
        case -2:
            return "varnum overflow";
        case -3:
            return "network failed connect";
        case -4:
            return "network timeout";
        case -5:
            return "network failed read";
        case -6:
            return "network failed write";
        case -7:
            return "Remaining length overflow";
        case -8:
            return "Length mismatch";
        case -9:
            return "missing or wrong packet";
        case -10:
            return "connection denied";
        case -11:
            return "failed subscription";
        case -12:
            return "suback array overflow";
        case -13:
            return "pong timeout";
    }
}

class MQTT {
private:
  WiFiClient wifiClient;
  MQTTClient mqttClient;
  String broker;
  int port;
  String username;
  String password;
  String id;
  String responsePayload;
  bool response;

public:
  MQTT(const String &broker, int port, const String &username,
       const String &password, const String &id)
      : username(username), password(password), id(id), broker(broker),
        port(port),response(false) {}
  bool connect(){
    mqttClient.begin(broker.c_str(), port, wifiClient);
    mqttClient.onMessage([this](auto& topic,auto& payload){
        Serial.println("Received mqtt message");
        Serial.println(topic);
        Serial.println(payload);
        this->responsePayload=payload;
        this->response=true;
    });
    Serial.print("Connecting to MQTT with: ");
    Serial.print(" broker: ");
    Serial.print(broker);
    Serial.print(" port: ");
    Serial.print(port);
    Serial.print(" id: ");
    Serial.print(id);
    Serial.print(" username:");
    Serial.print(username);
    Serial.print(" password:");
    Serial.println(password);
    for(int i=0;i<5;++i){
      if(mqttClient.connect(id.c_str(), username.c_str(), password.c_str()))return true;
      Serial.println(".");
      delay(1000);
    }
    Serial.println(getMQTTError(mqttClient.lastError()));
    Serial.println(mqttClient.returnCode());
    return false;
  }
  bool ensureConnected(){
      if(!mqttClient.connected()){
          Serial.println("The mqtt was disconnected");
          return connect();
      }
      return true;
  }
  void publish(const String &topic, const String &message){
    mqttClient.publish(topic, message);
  }
  void loop(){
      mqttClient.loop();
  }
  void publishJson(const String& topic,const JsonDocument& json){
      String message;
      serializeJson(json,message);
      publish(topic,message);
  }
  bool recv(const String& topic,String& result) {
      int counter=0;
      const int max_attempts=10;
      while(!mqttClient.subscribe(topic,2)){
          Serial.println("Failed subscribing for response");
          if(++counter==max_attempts)return false;
          delay(1000);
      }
      counter=0;
      while(!response){
          Serial.println("Waiting for server response");
          mqttClient.loop();
          if(++counter==max_attempts)return false;
          delay(1000);
      }
      counter=0;
      while(!mqttClient.unsubscribe(topic)){
          Serial.println("Failed unsubscribing from topic");
          if(++counter==max_attempts)return false;
          delay(1000);
      }
      result=this->responsePayload;
      this->response=false;
      return true;
  }
  bool recvJson(const String& topic,JsonDocument &result) {
      String message;
      bool ok=recv(topic,message);
      deserializeJson(result,message);
      return ok;
  }
};
