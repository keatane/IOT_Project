#define XSTR(s) STR(s)
#define STR(s) #s


#ifndef MQTT_USER
#error Missing MQTT_USER flag
#endif
#ifndef MQTT_PASS
#error Missing MQTT_PASS flag
#endif
#ifndef JUG_ID
#error Missing JUG_ID flag
#endif

#define MQTT_USERNAME XSTR(MQTT_USER)
#define MQTT_PASSWORD XSTR(MQTT_PASS)
