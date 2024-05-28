if(!process.env["MQTT_USERNAME"]||!process.env["MQTT_PASSWORD"]){
    console.error("Missing environment variables");
    process.exit(1);
}

export const MQTT_USERNAME=process.env["MQTT_USERNAME"];
export const MQTT_PASSWORD=process.env["MQTT_PASSWORD"];
