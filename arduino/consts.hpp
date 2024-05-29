#define XSTR(s) STR(s)
#define STR(s) #s
#define HOTSPOT_SSID "jug_" XSTR(JUG_ID)
#define FLOW_SENSOR 2

#define SERVER_ADDRESS IPAddress(192, 168, 4, 1)
#define SERVER_PORT 8080

#define MQTT_BROKER "212.78.1.205"
#define MQTT_PORT 1883

#define MQTT_ID "jug" XSTR(JUG_ID)
#define MQTT_SENSOR_TOPIC "/Thingworx/Jug" XSTR(JUG_ID) "/litresPerSecond"
#define MQTT_TOKEN_TOPIC "/jug/pair"
