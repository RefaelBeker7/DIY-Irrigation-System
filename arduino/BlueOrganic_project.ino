/* Date: 18/4/19 */
#include <virtuabotixRTC.h>
#include <TM1637Display.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>
 
SoftwareSerial hc06(9,8);
void createPrograms(String dataJson, int countPro);
void checkOpenTap(int countOfPrograms);
void RTC_example_function();
int checkCount(String dataProgram);
class PROGRAM{
  public:
    int timeHour;
    int timeMin;
    int duration;
    boolean days[7] = {false, false, false, false, false, false, false};
  };
  
// Module connection pins (Digital Pins)
#define CLK 11
#define DIO 10
uint8_t dataTM[] = { 0xff, 0xff, 0xff, 0xff };
const uint8_t SEG_DONE[] = {
  SEG_B | SEG_C | SEG_D | SEG_E | SEG_G,           // d
  SEG_A | SEG_B | SEG_C | SEG_D | SEG_E | SEG_F,   // O
  SEG_C | SEG_E | SEG_G,                           // n
  SEG_A | SEG_D | SEG_E | SEG_F | SEG_G            // E
  };
const uint8_t SEG_OPEN[] = {
SEG_A | SEG_B | SEG_C | SEG_D | SEG_E | SEG_F,   // O
SEG_E | SEG_F | SEG_A | SEG_B | SEG_G,           // P
SEG_A | SEG_D | SEG_E | SEG_F | SEG_G,           // E
SEG_C | SEG_E | SEG_G                            // n
};
TM1637Display tm1637(CLK, DIO);
// real time clock pins
#define RTC_RST    4
#define RTC_DATA  3
#define RTC_SCLK  2
//led pin
#define LED  6 
 int countPrograms = 0;
 char BluetoothData;
 int countOfChecks = 0;
 String data;
 char recieved;
 StaticJsonBuffer<512> jsonBuffer;
 PROGRAM programs[7];
/*
 this is a constructor for the real time clock
  object we are about to use to get time and date data.
  
  The time and date have been calibrated and would not lose
  sync even when module is not powered
*/
virtuabotixRTC RTC(RTC_SCLK, RTC_DATA, RTC_RST);  
/* !!!DO NOT TOUCH THIS!!! */
/*
  bluetooth name: HC-06
  bluetooth baud rate: 9600
  bluetooth password: 1234

  bluetooth is connected to pins TX and RX and therefor wan be written to and read
  from Serial stream object using "Serial.read()" and "Serial.print()" commands
  remember to begin serial communication with "Serial.begin(9600)" command
*/

void setup() {
  Serial.begin(9600);
  hc06.begin(9600);
  pinMode(LED, OUTPUT);
  tm1637.setBrightness(0x0f);
  tm1637.setSegments(dataTM);
  RTC_example_function();
}

void loop() {
  RTC.updateTime();
  // if there's a new command reset the string
  if(hc06.available() > 0){
      BluetoothData = hc06.read();
      Serial.println(BluetoothData);
    /* check if we send to open/close tap */
    if(BluetoothData == '1'){   //Open tap
      SetLed(HIGH);
      tm1637.setSegments(SEG_OPEN);
      Serial.println("LED #13 ON");
    } 
    else if(BluetoothData == '0'){ // Close Tap
      SetLed(LOW);
      tm1637.setSegments(dataTM);
      Serial.println("LED #13 OFF");
    } /* Now we send Json */
    else if(BluetoothData == '3'){ // Send Json
       clearObjectPrograms(countPrograms); /* clear Array Object Programs*/
       data = hc06.readString();     
       delay(100);
       Serial.println(data);
       countPrograms = checkCount(data);
         // ---- check---- 
        createPrograms(data, countPrograms);
        hc06.flush();
        hc06.end();    // Ends the serial communication once all data is received
        hc06.begin(9600);
      }
    }
    /* check if open tap */
    if(countOfChecks>4){
      countOfChecks -= 4;
      checkOpenTap(countPrograms);
    }else{
      countOfChecks++; 
    }
}
void checkOpenTap(int countOfPrograms){
    for(int i = 0; i<countPrograms; i++){
      Serial.println("Start check Programs ");
      delay(250);
      /* check if today have program*/
      if(checkDay(programs[0].days)){
        Serial.println(RTC.minutes);
        /* check the time */
        Scheduled_Blink(programs[i].timeHour, programs[i].timeMin, programs[i].duration);
        delay(100);
      }
  }
}

boolean checkDay(boolean days[]){
  for(int i =0; i<7; i++){
    if(days[i] && i== RTC.dayofweek){
      Serial.println(RTC.dayofweek);
      return true;
    }
  }
  return false;
}
/* reset The Object */
void clearObjectPrograms(int countPro){
  for(int i = 0; i<countPro; i++){
    for(int j = 0; j<7; j++){
        programs[i].days[j] = false;
     }
     programs[i].duration = 0;
     programs[i].timeHour = 0;
     programs[i].timeMin = 0;
  }
}
/* Create Obj Program From No' of Json with Parse */
void createPrograms(String dataJson, int countPro){
  JsonObject& rootJson = jsonBuffer.parseObject(dataJson);
    // now we save the dataJSON in the Array Object
    for(int i = 0; i<countPro; i++){
      Serial.println("Program "+String(i));
      JsonObject& n1 = rootJson["pro"+String(i)];
      String startTime = n1["t"];
      if(startTime == ""){
        Serial.println("somting go worng...");
        return;
        }
      String duration = n1["d"];
      String daysData = n1["y"];
      Serial.println(daysData);
      for(int j = 0; j<7; j++){
         if(daysData.indexOf(String(j)) != -1){
          programs[i].days[j] = true;
         }
         delay(100);
      }
      daysData = "";
      Serial.println(duration);
      programs[i].duration = duration.substring(2,4).toInt();
      duration = "";
      delay(100);
      programs[i].timeHour = startTime.substring(2,4).toInt();
      programs[i].timeMin = startTime.substring(6,8).toInt();
      Serial.println(startTime);
      delay(100);
    } 
    // Done! set...
    tm1637.setSegments(SEG_DONE);
    jsonBuffer.clear();
}
/* count how much programs have in the string */
int checkCount(String dataProgram){
   int result = 0;
   for(int i = 0; i<dataProgram.length(); i++){
       if(data.indexOf("pro"+String(i)) == -1)
          {
            Serial.println("pro"+String(i));
            break; 
          }
          else{
            result++;
          }
    }
  return result;
}
/* for open tap! */
void blinkLEDTapOpen(unsigned long time_Milliseconds)
{
  time_Milliseconds *= 60000;
  Serial.println(time_Milliseconds);
  Serial.println("Tap Open");
  tm1637.setSegments(SEG_OPEN); // set open tm1637
  SetLed(HIGH);
  delay(time_Milliseconds);
  SetLed(LOW);
  tm1637.setSegments(dataTM); // set 0000 tm1637
  Serial.println("Tap Close");
}
void SetLed(bool state)
{
  digitalWrite(LED, state);
}

void Scheduled_Blink(int HOUR, int MINUTE, int DURATION)
{
  if ((RTC.hours + 1) == HOUR && RTC.minutes == MINUTE)
  {
    Serial.print("check time true.. ");
    blinkLEDTapOpen(DURATION);
  }
}
void RTC_example_function()
{
  RTC.updateTime();   // updates all time and date data for object
  Serial.print(RTC.hours + 1);
  Serial.print(":");
  Serial.print(RTC.minutes);
  Serial.print(":");
  Serial.print(RTC.seconds);
  Serial.print("    ");
  Serial.print(RTC.year);
  Serial.print(":");
  Serial.print(RTC.month);
  Serial.print(":");
  Serial.print(RTC.dayofmonth);
  Serial.print("   day of week:");
  Serial.print(RTC.dayofweek);
  Serial.println();

  //Scheduled_Blink(20, 57, 30,2000);
  delay(1000);
}
