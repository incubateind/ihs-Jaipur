
#include <ESP8266WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include<String.h>
#include<iostream>



IPAddress server_addr(192,168,43,239);  // IP of the MySQL *server* here
char user[] = "root";              // MySQL user login username
char password[] = "root";        // MySQL user login password

char accelerometer_z[]="accelerometer_z";


char buffer[100];

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

  

  
}

void loop(){
 
double Zread = analogRead(A0);
 double Gz=Zread/67.584;
 Serial.print("Z - Coordinate :");
Serial.println(Gz);
delay(10);




      sprintf(buffer,"UPDATE nistkota_nistdb.aurdino SET value='%lf' where sensor = '%s' ",Gz,accelerometer_z);
    cursor->execute(buffer);
   
   
    
    
    
  delay(10);
}
