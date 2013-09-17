byte pins_sensors[] = { 
  2, 3, 4, 5, 6, 7, 8, 9
};
byte pins_relays[] = { 
  10, 11, 12, 13, 14, 15, 16, 17
};

byte pins_light[] = { 
  18, 19
};

#define MAX_LANES          8
#define BITS_LIGHT         2

int t;

void setup() {
  Serial.begin(57600);
  //lanes
  for (t=0; t<MAX_LANES; t++) {
    //config sensor
    pinMode(pins_sensors[t], INPUT);
    digitalWrite(pins_sensors[t], HIGH); //pull-up resistor active (maintain high-level)
    //config relay
    pinMode(pins_relays[t], OUTPUT);
    digitalWrite(pins_relays[t], LOW); //keep relays off (relay on = track power off)
  }
  //light
  for (t=0; t<BITS_LIGHT; t++) {
    //config relay
    pinMode(pins_relays[t], OUTPUT);
    digitalWrite(pins_light[t], HIGH); //leave lighs fully on
  }
}


#define D_ON 19
#define D_OFF 30

int don ;
void loop() {
  don = 10;
  while (don <= 100) {
    digitalWrite(13, LOW);
    delay(don);
    digitalWrite(13, HIGH);
    delay(D_OFF);
    don+=10;
  }
  digitalWrite(13, LOW);
  delay(5000);
  digitalWrite(13, HIGH);
  delay(1000);
}






