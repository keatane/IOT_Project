#include <Wire.h>
// #include <LiquidCrystal_I2C.h>

// LiquidCrystal_I2C lcd(0x27, 20, 4); // LCD2004

volatile int NbTopsFan; // Measuring the rising edges of the signal
const double C=1/(60*10); // litres/spin

const int hallsensor = 2; // The pin location of the sensor: D2

void rpm() { // Interrupt service routine (ISR) for rising edge
  NbTopsFan++;
}

void setup() {
  Serial.begin(9600);
  pinMode(hallsensor, INPUT);
  attachInterrupt(digitalPinToInterrupt(hallsensor), rpm, RISING); // Attach interrupt
}

void loop() {
  // NbTopsFan = 0; // Reset NbTopsFan for new measurement

  // sei();        // Enable interrupts
  delay(1000);  // Wait for 1 second (measurement period)
  // cli();        // Disable interrupts

  // Calc = (NbTopsFan * 60) / 7.5; // Calculate flow rate (L/hour)

  Serial.print(NbTopsFan, DEC);
  Serial.println(" spins");
  Serial.print(NbTopsFan*C, DEC);
  Serial.println(" litres");
}

