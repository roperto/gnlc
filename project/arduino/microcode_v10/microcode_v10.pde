// MAGIC NUMBERS
#define FIRMWARE_VERSION   10
#define MAX_LANES          8
#define BITS_LIGHT         2

//STATES
#define STATE_MAY_BLOCK        1
#define STATE_MAY_NOT_UNBLOCK  2
#define STATE_MAY_UNBLOCK      3
#define STATE_MAY_NOT_BLOCK    4

//TIMERS
#define TIME_MINIMUM_OVER_SENSOR            500 // 0.5ms
#define TIME_MAXIMUM_OVER_SENSOR            500e3 // 0.5s
#define TIME_MINIMUM_BETWEEN_LAPS  1e6 // 1s

// MESSAGES
#define MASK_MESSAGE                      (byte)0xF8 // 11111.000
#define MASK_PARAMETER                    (byte)0x07 // 00000.111
#define P2A_RESERVED_00                   (byte)0x00 // 00000.---
#define A2P_INFORM_ERROR                  (byte)0x00 // 00000.---
#define P2A_REQUEST_RESET                 (byte)0x08 // 00001.---
#define A2P_INFORM_RESETED                (byte)0x08 // 00001.---
#define P2A_REQUEST_VERSION               (byte)0x10 // 00010.---
#define A2P_INFORM_VERSION                (byte)0x10 // 00010.---
#define P2A_REQUEST_PINS                  (byte)0x18 // 00011.---
#define A2P_INFORM_PINS                   (byte)0x18 // 00011.--- + byte (low 0-7) + byte (mid 8-15) + byte (high 16-19)
#define P2A_SET_PIN_STATE                 (byte)0x20 // 00100.--- (param=set to false if 0, true otherwise) + byte (pin to set)
#define A2P_RESERVED_20                   (byte)0x20 // 00100.---
#define P2A_REQUEST_TIMING_INFO           (byte)0x28 // 00100.---
#define A2P_INFORM_TIMING_INFO            (byte)0x28 // 00101.--- (param=inform uptime if 0, LPS otherwise) + long (info)
#define A2P_INFORM_TIMING_INFO__UPTIME    (byte)0x28 // 00101.000
#define A2P_INFORM_TIMING_INFO__LPS       (byte)0x29 // 00101.001
#define P2A_RESERVED_30                   (byte)0x30 // 00110.---
#define A2P_INFORM_LAP                    (byte)0x30 // 00110.--- (param=sensor) + long (block time) + long (lap time)
#define P2A_REQUEST_SET_ALL_RELAYS        (byte)0x38 // 00111.--- (param=set to false if 0, true otherwise)
#define P2A_REQUEST_SET_ALL_RELAYS__OFF   (byte)0x38 // 00111.000
#define P2A_REQUEST_SET_ALL_RELAYS__ON    (byte)0x39 // 00111.001
#define A2P_RESERVED_38                   (byte)0x38 // 00111.---
#define P2A_REQUEST_RELAY_ON              (byte)0x40 // 01000.--- (param=relay)
#define A2P_RESERVED_40                   (byte)0x40 // 01000.000
#define P2A_REQUEST_RELAY_OFF             (byte)0x48 // 01001.--- (param=relay)
#define A2P_RESERVED_48                   (byte)0x48 // 01001.---
#define P2A_REQUEST_RELAY_PAUSE           (byte)0x50 // 01010.--- (param=stop if 0, resume otherwise)
#define P2A_REQUEST_RELAY_PAUSE__STOP     (byte)0x50 // 01010.000
#define P2A_REQUEST_RELAY_PAUSE__RESUME   (byte)0x51 // 01010.001
#define A2P_RESERVED_50                   (byte)0x50 // 01010.---
#define P2A_REQUEST_SET_LIGHTS            (byte)0x58 // 01011.--- (param=0xx, xx=lights)
#define A2P_RESERVED_58                   (byte)0x58

// PINS
byte pins_sensors[] = { 
  2, 3, 4, 5, 6, 7, 8, 9
};
byte pins_relays[] = { 
  10, 11, 12, 13, 14, 15, 16, 17
};

byte pins_light[] = { 
  18, 19
};

// TRACK VARIABLES
byte t; //for use in loops for tracks
byte state[MAX_LANES]; //which state is the track
boolean sensor_active[MAX_LANES]; //which sensors are making readings
unsigned long block_us[MAX_LANES]; //track microseconds the sensor was blocked
unsigned long lap_us[MAX_LANES]; //track lap time (microseconds)
unsigned long state_timer[MAX_LANES]; //timer user to change states

// COMMUNICATION  VARIABLES
unsigned long comm_next = 0; //next time to read communication data
byte comm_msg; //message part (first 4 bits)
byte comm_param; //parameter part (last 4 bits)

// LPS - LOOPS PER SECOND  VARIABLES
boolean lps = false; //LPS counter is active
unsigned long lps_next = 0; //next time to recalculate it
unsigned long lps_counting = 0; //counting number of loops
// DELME unsigned long lps_last_count = 0; //last count of LPS

// OTHER VARIABLES
unsigned long us; //tracks the microsecond of the beggining of the loop
byte tmp_byte; // for temp use
boolean sensor_pause[MAX_LANES]; //saves the state when pausing

// AUXILIARY FUNCTIONS
void reset() {
  //lanes
  for (t=0; t<MAX_LANES; t++) {
    //config sensor
    pinMode(pins_sensors[t], INPUT);
    digitalWrite(pins_sensors[t], HIGH); //pull-up resistor active (maintain high-level)
    //config relay
    pinMode(pins_relays[t], OUTPUT);
    digitalWrite(pins_relays[t], LOW); //keep relays off (relay on = track power off)
    //config lane
    state[t] = STATE_MAY_BLOCK;
    block_us[t] = lap_us[t] = state_timer[t] = 0;
    sensor_active[t] = true;
  }
  //light
  for (t=0; t<BITS_LIGHT; t++) {
    //config relay
    pinMode(pins_relays[t], OUTPUT);
    digitalWrite(pins_light[t], HIGH); //leave lighs fully on
  }
}

void send_uint(unsigned int ui) {
  Serial.write(ui);
  Serial.write(ui >> 8);
}

void send_ulong(unsigned long ul) {
  Serial.write(ul);
  Serial.write(ul >> 8);
  Serial.write(ul >> 16);
  Serial.write(ul >> 24);
}

// MAIN FUNCTIONS
void setup() {
  Serial.begin(57600);
  reset();
}




void loop() {


  //MICRO SECONDS LOOP STARTED (loops at around 70 mins -- max ulong = 4294967295 = 4.294967295e9)
  //WARNING: unpredictable problems here when this variable loops. Something to think about later...
  us = micros();

  //COUNT LOOPS PER SECOND (how many times the loop function is called in a second)
  if (lps) {
    lps_counting++;
    if (lps_next <= us) { // send result and stop
      Serial.write(A2P_INFORM_TIMING_INFO__LPS);
      send_ulong(lps_counting);
      lps=false;
    }
  }

  //CHECK SENSOR READINGS
  for (t=0; t<MAX_LANES; t++) {
    //if reading sensor of the track is not active, continue to next track
    if (! sensor_active[t]) continue;

    //check the state of the track
    switch (state[t]) {
    case STATE_MAY_BLOCK: //waiting sensor to get low (blocked by the car)
      if (!digitalRead(pins_sensors[t])) { //sensor low
        block_us[t] = us; //store what time the sensor got blocked (count blocked time)
        state[t] = STATE_MAY_NOT_UNBLOCK; //change to next state
        state_timer[t] = us + TIME_MINIMUM_OVER_SENSOR; //how long it may not unblock (the car must stay on the sensor)
        break;
      }
      break;
    case STATE_MAY_NOT_UNBLOCK: //if the signal unblocks in this state, it was probably some noise not the car itself
      if (state_timer[t] <= us) { //the minimum time elapsed
        state[t] = STATE_MAY_UNBLOCK; //now the car can unblock
        state_timer[t] = us + TIME_MAXIMUM_OVER_SENSOR; //how long to wait for the car to unblock the sensor (the car must leave the sensor)
        break;
      }
      if (digitalRead(pins_sensors[t])) { //sensor unblocked, it should not, it is probably not a correct reading. Ignore it.
        state[t] = STATE_MAY_BLOCK; //go back to the first state without counting a lap
        break;
      }
      break;
    case STATE_MAY_UNBLOCK: //now we expect the car to unblock the sensor
      if (digitalRead(pins_sensors[t])) { //the sensor is high (unblocked), count the lap!
        Serial.write(A2P_INFORM_LAP | t); //send the message with the track number as parameter (last 4 bits)
        send_ulong(us - block_us[t]); //send how many micro seconds the sensor was blocked
        send_ulong(us - lap_us[t]); //send how many micro seconds since the last lap
        lap_us[t] = us; //and store the time of this lap to calculate the time until the next lap
        state[t] = STATE_MAY_NOT_BLOCK; //next state, a cooldown before it can make another lap
        state_timer[t] = us + TIME_MINIMUM_BETWEEN_LAPS; //how long the car is not supposed to pass again (ie. two cars together in the track)
        break;
      }
      if (state_timer[t] <= us) { //the sensor did not unblock in time. (ie. something fell over the sensors)
        state[t] = STATE_MAY_NOT_BLOCK; //wait until it unblocks to start waiting for the car again
        break;
      }
      break;
    case STATE_MAY_NOT_BLOCK: //waiting for the minimum time and the sensor is not blocked
      if ((state_timer[t] <= us) && (digitalRead(pins_sensors[t]))) { //condition met (timer and sensor unblocked)
        state[t] = STATE_MAY_BLOCK; //go back to first state, waiting for the car
        break;
      }
      break;
    default: //unknow state, should never happen
      Serial.write(A2P_INFORM_ERROR); //send error message
      state[t] = STATE_MAY_BLOCK; //try to fix it going to first state
      state_timer[t] = 0; //reset the timer too
      break;
    }
  }

  // COMMUNICATION
  if (comm_next <= us) {
    //time between communication readings
    comm_next += 100e3;

    //only if there is available
    while (Serial.available() > 0) {
      //get message and parameter
      comm_msg = Serial.read();
      comm_param = comm_msg & MASK_PARAMETER;
      comm_msg &= MASK_MESSAGE;
      //check message
      switch (comm_msg) { //check the message

      case P2A_REQUEST_RESET://reset all pin states
        reset();
        Serial.write(A2P_INFORM_RESETED);
        break;

      case P2A_REQUEST_VERSION: //send the version of this firmware
        Serial.write(A2P_INFORM_VERSION);
        Serial.write(FIRMWARE_VERSION);
        break;

      case P2A_REQUEST_PINS: // sends all pins readings
        Serial.write(A2P_INFORM_PINS);
        tmp_byte = 0;
        if (digitalRead(0)) tmp_byte |= 0x01;
        if (digitalRead(1)) tmp_byte |= 0x02;
        if (digitalRead(2)) tmp_byte |= 0x04;
        if (digitalRead(3)) tmp_byte |= 0x08;
        if (digitalRead(4)) tmp_byte |= 0x10;
        if (digitalRead(5)) tmp_byte |= 0x20;
        if (digitalRead(6)) tmp_byte |= 0x40;
        if (digitalRead(7)) tmp_byte |= 0x80;
        Serial.write(tmp_byte);
        tmp_byte = 0;
        if (digitalRead(8)) tmp_byte |= 0x01;
        if (digitalRead(9)) tmp_byte |= 0x02;
        if (digitalRead(10)) tmp_byte |= 0x04;
        if (digitalRead(11)) tmp_byte |= 0x08;
        if (digitalRead(12)) tmp_byte |= 0x10;
        if (digitalRead(13)) tmp_byte |= 0x20;
        if (digitalRead(14)) tmp_byte |= 0x40;
        if (digitalRead(15)) tmp_byte |= 0x80;
        Serial.write(tmp_byte);
        tmp_byte = 0;
        if (digitalRead(16)) tmp_byte |= 0x01;
        if (digitalRead(17)) tmp_byte |= 0x02;
        if (digitalRead(18)) tmp_byte |= 0x04;
        if (digitalRead(19)) tmp_byte |= 0x08;
        Serial.write(tmp_byte);
        break;

      case P2A_SET_PIN_STATE: //read one more byte and set the pin
        tmp_byte = Serial.read();
        digitalWrite(tmp_byte, comm_param);
        break;

      case P2A_REQUEST_TIMING_INFO: //enable LPS couting, send uptime
        if (!lps) {        
          lps = true;
          lps_next = us + 1e6;
          lps_counting = 0;
        }
        Serial.write(A2P_INFORM_TIMING_INFO__UPTIME);
        send_ulong(us);
        break;

      case P2A_REQUEST_SET_ALL_RELAYS: //set all relays to param
        for (t=0; t<MAX_LANES; t++) {
          digitalWrite(pins_relays[t], comm_param);
        }
        break;

      case P2A_REQUEST_RELAY_ON: //set param relay to on
        digitalWrite(pins_relays[comm_param], HIGH);
        break;

      case P2A_REQUEST_RELAY_OFF: //set param relay to off
        digitalWrite(pins_relays[comm_param], LOW);
        break;

      case P2A_REQUEST_RELAY_PAUSE: //pause/restore relays
        if (comm_param) { //restore
          for (t=0; t<MAX_LANES; t++) {
            digitalWrite(pins_relays[t], sensor_pause[t]);
          }
        }
        else { //pause
          for (t=0; t<MAX_LANES; t++) {
            sensor_pause[t] = digitalRead(pins_relays[t]);
            digitalWrite(pins_relays[t], HIGH);
          }
        }
        break;

      case P2A_REQUEST_SET_LIGHTS: //set lights
        digitalWrite(pins_light[0], comm_param & 1);
        digitalWrite(pins_light[1], comm_param & 2);
        break;

      default:
        Serial.write(A2P_INFORM_ERROR);
        break;
      }
    }
  }
}




