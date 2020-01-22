
#include <ESP8266WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include<String.h>
#include<iostream>



IPAddress server_addr(192,168,43,239);  // IP of the MySQL *server* here
char user[] = "root";              // MySQL user login username
char password[] = "root";        // MySQL user login password
char ultrasonic[]="ultrasonic";
char accelerometer_x[]="accelerometer_x";
int ultrasonic_value=5;
int led = D5 ;

char buffer[100];

#define TRIGGER 0
#define ECHO    4

// Sample query
char INSERT_SQL[] = "UPDATE nistkota_nistdb.aurdino SET value='10' where sensor = 'ultrasonic' ";


// WiFi card example
char ssid[] = "arpit";         // your SSID
char pass[] = "higoogle";     // your SSID Password


WiFiClient client;                 // Use this for WiFi instead of EthernetClient
MySQL_Connection conn(&client);
MySQL_Cursor* cursor;

void setup(){



    Serial.begin(115200);
  while (!Serial); // wait for serial port to connect. Needed for Leonardo only

  if(!SPIFFS.begin()){
    Serial.println("An Error has occurred while mounting SPIFFS");
    return;
  }
  // Begin WiFi section
  Serial.printf("\nConnecting to %s", ssid);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(10);
    Serial.print(".");
  }

  // print out info about the connection:
  Serial.println("\nConnected to network");
  Serial.print("My IP address is: ");
  Serial.println(WiFi.localIP());

  Serial.print("Connecting to SQL...  ");
  if (conn.connect(server_addr, 3306, user, password))
    Serial.println("OK.");
  else
    Serial.println("FAILED.");
  
  // create MySQL cursor object
  cursor = new MySQL_Cursor(&conn);

     pinMode(A0,INPUT);

   pinMode(TRIGGER, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(led,OUTPUT);


  
}

void loop(){
 
double Xread = analogRead(A0);
 double Gx=Xread/67.584;
 Serial.print("X - Coordinate :");
Serial.println(Gx);
delay(10);


       long duration, distance;
  digitalWrite(TRIGGER, LOW);  
  delayMicroseconds(2); 
  
  digitalWrite(TRIGGER, HIGH);
  delayMicroseconds(10); 
  
  digitalWrite(TRIGGER, LOW);
  duration = pulseIn(ECHO, HIGH);


 ultrasonic_value = (duration/2) / 29.1;
 if(ultrasonic_value<50)
 {
   digitalWrite(led,HIGH);
   delay(150);
   digitalWrite(led,LOW);
   delay(10);
  
  }

 
    Serial.print("Centimeter:");
  Serial.println(ultrasonic_value);

      sprintf(buffer,"UPDATE nistkota_nistdb.aurdino SET value='%d' where sensor = '%s' ",ultrasonic_value,ultrasonic);
    cursor->execute(buffer);

      sprintf(buffer,"UPDATE nistkota_nistdb.aurdino SET value='%lf' where sensor = '%s' ",Gx,accelerometer_x);
    cursor->execute(buffer);
   
   
    
    
    
  delay(10);
}
