#include <TinyGPS++.h>
#include <SoftwareSerial.h>-
#define BLYNK_PRINT Serial
#include <ESP8266WiFi.h>
#include <BlynkSimpleEsp8266.h>

static const int RXPin = 4, TXPin = 5;
static const uint32_t GPSBaud = 9600;
int a=13;
TinyGPSPlus gps;
WidgetMap myMap(V0);
SoftwareSerial ss(RXPin, TXPin); 

BlynkTimer timer;
float spd;
float sats;
String bearing;

char auth[] = "RuiuXjdjHNad9nWFLkjXD2675k21LUTB"; 
char ssid[] = "Aditya Bansal";                                        
char pass[] = "1357924680";  

unsigned int move_index = 1;
  
void setup()
{
  Serial.begin(115200);
  Serial.println();
  ss.begin(GPSBaud);
  Blynk.begin(auth, ssid, pass);
  timer.setInterval(5000L, checkGPS);  
  pinMode(a,INPUT); 
}
void checkGPS(){
  if (gps.charsProcessed() < 10)
  {
    Serial.println(F("No GPS detected: check wiring."));
      Blynk.virtualWrite(V4, "GPS ERROR");
  }
}

void loop()
{
  if (digitalRead(a)==LOW)
  {
  while (ss.available() >0)
  {
    if(gps.encode(ss.read()))
    displayInfo();
  }
  if (gps.location.isUpdated())
    {
       if (gps.encode(ss.read()))
        displayInfo();
      Serial.print("Latitude= "); 
      Serial.print(gps.location.lat(), 6);
      Serial.print(" Longitude= "); 
      Serial.println(gps.location.lng(), 6);
    }
  } 
Blynk.run();
timer.run();
}
void displayInfo()

{
  if (gps.location.isValid() ) 
  {
    
    float latitude = (gps.location.lat());     //Storing the Lat. and Lon. 
    float longitude = (gps.location.lng()); 
    
    Serial.print("LAT:  ");
    Serial.println(latitude, 6);
    Serial.print("LONG: ");
    Serial.println(longitude, 6);
    Blynk.virtualWrite(V1, String(latitude, 2));   
    Blynk.virtualWrite(V2, String(longitude, 2));  
    myMap.location(move_index, latitude, longitude, "GPS_Location");
    spd = gps.speed.kmph();               //get speed
       Blynk.virtualWrite(V3, spd);
       
       sats = gps.satellites.value(); 
         bearing = TinyGPSPlus::cardinal(gps.course.value()); // get the direction
       Blynk.virtualWrite(V5, bearing);               
}
Serial.println();  
}
