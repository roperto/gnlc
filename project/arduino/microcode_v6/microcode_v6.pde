//MAGIC NUMBERS
#define FIRMWARE_VERSION  6
#define SENSORS_ANALOG    6
#define SENSORS_DIGITAL  20
#define TRACKS            8

//MESSAGE FORMAT - 8bits: mmmmpppp (message / parameter) - 0xAB where A is the msg, B must be 0 (let the parameter clear)
#define P2A_PING             // 0x00 - 0000.0000 + ulong
#define P2A_REQUEST_VERSION  // 0x01 - 0000.0001 + uint
#define P2A_RESERVED_02      // 0x02 - 0000.0002 [EOM]

#define A2P_PONG             // 0x00 - 0000.0000 + ulong
#define A2P_INFORM_VERSION   // 0x01 - 0000.0001 + uint
#define A2P_INFORM_ERROR     // 0x02 - 0000.0002 [EOM]

#define MSG_RESPOND_PING     // 0x00 - 0000.0000 + long
#define MSG_REQUEST_VERSION  // 0x01 - 0000.0001 + 

#define MSG_DEBUG_ERROR    0x10
#define MSG_DEBUG_SENSORS  0x20
#define MSG_DEBUG_TIMING   0x30
#define MSG_VERSION        0x50
#define MSG_RELAY_ON       0x60
#define MSG_RELAY_OFF      0x70
#define MSG_SENSOR_ON      0x80
#define MSG_SENSOR_OFF     0x90
#define MSG_LAP            0xF0

//STATES
#define STATE_MAY_BLOCK        1
#define STATE_MAY_NOT_UNBLOCK  2
#define STATE_MAY_UNBLOCK      3
#define STATE_MAY_NOT_BLOCK    4

//PINS
byte pins_readings[] = { 
  19, 18, 17, 16, 15, 14, 11, 10
};
byte pins_relays[] = { 
  2, 3, 4, 5, 6, 7, 8, 9
};

// AUXILIARY FUNCTIONS
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

// TRACK VARIABLES
byte t; //for use in loops for tracks
byte state[TRACKS]; //which state is the track
boolean sensor_active[TRACKS]; //which sensors are making readings
unsigned long block_us[TRACKS]; //track microseconds the sensor was blocked
unsigned long lap_us[TRACKS]; //track lap time (microseconds)
unsigned long state_timer[TRACKS]; //timer user to change states

// LPS - LOOPS PER SECOND
boolean lps = true; //LPS counter is active
unsigned long lps_next = 0; //next time to recalculate it
unsigned long lps_counting = 0; //counting number of loops
unsigned long lps_last_count = 0; //last count of LPS

// COMMUNICATION
unsigned long comm_next = 0; //next time to read communication data
unsigned long comm_msg; //message part (first 4 bits)
unsigned long comm_param; //parameter part (last 4 bits)

// OTHER VARIABLES
unsigned long us; //tracks the microsecond of the beggining of the loop
unsigned long temp_ulong; //temporary variable for local use when needed


// SETUP FUNCTION - Runs once at power on
void setup() {
  //start serial comunication
  Serial.begin(57600);
  //inicialize tracks
  for (t=0; t<TRACKS; t++) {
    //variables
    state[t] = STATE_MAY_BLOCK;
    block_us[t] = lap_us[t] = state_timer[t] = 0;
    sensor_active[t] = false;
    //setup pins
    pinMode(pins_readings[t], INPUT);
    pinMode(pins_relays[t], OUTPUT);
    digitalWrite(pins_readings[t], HIGH); //pull-up resistor active (maintain high-level)
    digitalWrite(pins_relays[t], LOW); //keep relays off (relay on = track power off)
  }
}

void loop() {
  //MICRO SECONDS LOOP STARTED (loops at around 70 mins -- max ulong = 4294967295 = 4.294967295e9)
  //WARNING: unpredictable problems here when this variable loops. Something to think about later...
  us = micros();

  //COUNT LOOPS PER SECOND (how many times the loop function is called in a second)
  if (lps) {
    lps_counting++;
    if (lps_next <= us) { //calculate and store the value every second
      lps_next = us + 1e6;
      lps_last_count = lps_counting;
      lps_counting = 0;
    }
  }

  //CHECK SENSOR READINGS
  for (t=0; t<TRACKS; t++) {
    //if reading sensor of the track is not active, continue to next track
    if (! sensor_active[t]) continue;

    //check the state of the track
    switch (state[t]) {
    case STATE_MAY_BLOCK: //waiting sensor to get low (blocked by the car)
      if (!digitalRead(pins_readings[t])) { //sensor low
        block_us[t] = us; //store what time the sensor got blocked (count blocked time)
        state[t] = STATE_MAY_NOT_UNBLOCK; //change to next state
        state_timer[t] = us + 1e3; //how long it may not unblock (the car must stay on the sensor)
        break;
      }
      break;
    case STATE_MAY_NOT_UNBLOCK: //if the signal unblocks in this state, it was probably some noise not the car itself
      if (state_timer[t] <= us) { //the minimum time elapsed
        state[t] = STATE_MAY_UNBLOCK; //now the car can unblock
        state_timer[t] = us + 500e3; //how long to wait for the car to unblock the sensor (the car must leave the sensor)
        break;
      }
      if (digitalRead(pins_readings[t])) { //sensor unblocked, it should not, it is probably not a correct reading. Ignore it.
        state[t] = STATE_MAY_BLOCK; //go back to the first state without counting a lap
        break;
      }
      break;
    case STATE_MAY_UNBLOCK: //now we expect the car to unblock the sensor
      if (digitalRead(pins_readings[t])) { //the sensor is high (unblocked), count the lap!
        Serial.write(MSG_LAP | t); //send the message with the track number as parameter (last 4 bits)
        send_ulong(us - block_us[t]); //send how many micro seconds the sensor was blocked
        send_ulong(us - lap_us[t]); //send how many micro seconds since the last lap
        lap_us[t] = us; //and store the time of this lap to calculate the time until the next lap
        state[t] = STATE_MAY_NOT_BLOCK; //next state, a cooldown before it can make another lap
        state_timer[t] = us + 500e3; //how long the car is not supposed to pass again (ie. two cars together in the track)
        break;
      }
      if (state_timer[t] <= us) { //the sensor did not unblock in time. (ie. something fell over the sensors)
        state[t] = STATE_MAY_NOT_BLOCK; //wait until it unblocks to start waiting for the car again
        break;
      }
      break;
    case STATE_MAY_NOT_BLOCK: //waiting for the minimum time and the sensor is not blocked
      if ((state_timer[t] <= us) && (digitalRead(pins_readings[t]))) { //condition met (timer and sensor unblocked)
        state[t] = STATE_MAY_BLOCK; //go back to first state, waiting for the car
        break;
      }
      break;
    default: //unknow state, should never happen
      Serial.write(MSG_DEBUG_ERROR); //send error message
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
      comm_param = comm_msg & 0x0F;
      comm_msg &= 0xF0;
      //check message
      switch (comm_msg) { //check the message
      case MSG_DEBUG_ERROR: //nothing to do if we receive an error message
        break;
      case MSG_DEBUG_SENSORS: //asking for all the sensor readings.
        Serial.write(MSG_DEBUG_SENSORS);
        //prepare and send digital readings (32 bits, 4 bytes)
        temp_ulong = 0;
        for (t=0; t<SENSORS_DIGITAL; t++) {
          if (digitalRead(t)) {
            temp_ulong |= (1 << t); //fill an ulong (32 bits) with the sensor's readings
          }
        }
        send_ulong(temp_ulong);
        //send analog readings, mapping from 0-1023 to 0-255 (1 byte each sensor)
        for (t=0; t<SENSORS_ANALOG; t++) Serial.write((byte)map(analogRead(t), 0, 1023, 0, 255));
        break;
      case MSG_DEBUG_TIMING: //send information about timing
        Serial.write(MSG_DEBUG_TIMING);
        send_ulong(millis());
        send_ulong(lps_last_count);
        break;
      case MSG_VERSION: //send the version of this firmware
        Serial.write(MSG_VERSION);
        Serial.write(FIRMWARE_VERSION);
        break;
      case MSG_RELAY_ON: //turn on relay for track (parameter). Turn on relay = power off track.
        if (comm_param >= TRACKS) break; //abort if parameter is invalid
        digitalWrite(pins_relays[comm_param], HIGH); //turn output on for the parameter part of the reading
        Serial.write(MSG_RELAY_ON | comm_param); //send back the confirmation
        break;
      case MSG_RELAY_OFF: //turn off relay for track (parameter). Turn off relay = power on track.
        if (comm_param >= TRACKS) break; //abort if parameter is invalid
        digitalWrite(pins_relays[comm_param], LOW); //turn output on for the parameter part of the reading
        Serial.write(MSG_RELAY_OFF | comm_param); //send back the confirmation
        break;
      case MSG_SENSOR_ON: //enable readings for track (parameter).
        if (comm_param >= TRACKS) break; //abort if parameter is invalid
        sensor_active[comm_param] = true; //set the track sensor reading on
        digitalWrite(pins_readings[comm_param], LOW); //turn off pullup resistor
        Serial.write(MSG_SENSOR_ON | comm_param); //send back the confirmation
        break;
      case MSG_SENSOR_OFF: //disable readings for track (parameter).
        if (comm_param >= TRACKS) break; //abort if parameter is invalid
        sensor_active[comm_param] = false; //set the track sensor reading off
        digitalWrite(pins_readings[comm_param], HIGH); //turn on pullup resistor
        Serial.write(MSG_SENSOR_OFF | comm_param); //send back the confirmation
        break;
      case MSG_LAP: //shouldn't receive that message.
        Serial.write(MSG_DEBUG_ERROR); //send error
        break;
      default: //invalid message, send error
        Serial.write(MSG_DEBUG_ERROR); //send error
        break;
      }
    }
  }
}




