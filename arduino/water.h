#include <Arduino.h>

namespace water {

const double SENSOR_C = 1.0 / (60 * 10); // litres/spin

volatile int spins;

void rpm() { spins++; }

class WaterSensor {
public:
  WaterSensor(int flow_sensor) {
    attachInterrupt(digitalPinToInterrupt(flow_sensor), rpm,
                    RISING); // Attach interrupt
  }

  double measure() {
    spins = 0;
    delay(1000);
    return spins * SENSOR_C;
  }
};

} // namespace water

using water::WaterSensor;
