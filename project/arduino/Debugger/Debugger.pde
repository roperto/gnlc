/*
  Blink
 Turns on an LED on for one second, then off for one second, repeatedly.
 
 This example code is in the public domain.
 */

byte lights = 0;

void setup() {                
  // initialize the digital pin as an output.
  // Pin 13 has an LED connected on most Arduino boards:
  for (int i=2; i<=9; i++) {
    pinMode(i, INPUT);
    digitalWrite(i, HIGH); //pullup
  }
  for (int i=10; i<=19; i++) {
    pinMode(i, OUTPUT);
    digitalWrite(i, LOW); //start off
  }
}

unsigned long ms = 0;

void loop() {
  if (ms < millis()) {
    lights++;
    if (lights >= 4) lights = 0;
    ms = millis() + 1000;
  }

  digitalWrite(18, lights & 2);
  digitalWrite(19, lights & 1);

  for (int i=2; i<=9; i++) {
    digitalWrite(i+8, !digitalRead(i));
  }
}



