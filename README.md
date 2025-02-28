# SafetyNetAlerts
The SafetyNet Alerts application is designed to send critical information to emergency services systems.

## Project Description   
This backend API serves as a communication bridge between the application and the emergency service teams. It allows retrieving, updating, and deleting data regarding citizens, their medical records, fire stations, and alerts for various emergencies.

The API offers endpoints to retrieve personal and medical data, alert specific services based on fire station coverage, and send notifications in case of an emergency. Additionally, it provides data about all citizens in a given city, facilitating the quick dissemination of vital information to first responders.

### Features:  
- Retrieve details about people covered by a fire station.  
- Get a list of children living in a specific address.  
- Get emergency contact phone numbers for residents.  
- Get a list of people at a given address with medical histories.  
- Get medical records for individuals based on their names.  
- Retrieve email addresses for all residents in a given city.  

### API Endpoints
#### 1. **Firestation Coverage**
   - **URL**: `/firestation?stationNumber=<station_number>`
   - **Method**: `GET`
   - **Description**: Returns a list of people covered by the fire station. Includes details like name, address, phone number, and the count of adults and children.
   
#### 2. **Child Alert**
   - **URL**: `/childAlert?address=<address>`
   - **Method**: `GET`
   - **Description**: Returns a list of children (18 or under) living at the given address, including their name, age, and household members.

#### 3. **Phone Alert**
   - **URL**: `/phoneAlert?firestation=<firestation_number>`
   - **Method**: `GET`
   - **Description**: Returns a list of phone numbers of all residents served by the specified fire station.

#### 4. **Fire**
   - **URL**: `/fire?address=<address>`
   - **Method**: `GET`
   - **Description**: Returns a list of people living at the specified address with their medical history (medications, dosages, allergies), as well as the fire station covering the area.

#### 5. **Flood Stations**
   - **URL**: `/flood/stations?stations=<list_of_station_numbers>`
   - **Method**: `GET`
   - **Description**: Returns a list of households served by the specified fire stations, grouped by address and with medical details.

#### 6. **Person Info by Last Name**
   - **URL**: `/personInfolastName=<lastName>`
   - **Method**: `GET`
   - **Description**: Retrieves information (name, address, age, medical records) of all people with the given last name.

#### 7. **Community Email**
   - **URL**: `/communityEmail?city=<city>`
   - **Method**: `GET`
   - **Description**: Returns a list of email addresses for all residents in the given city.


## Technologies Used
-Backend Framework: Spring Boot  
-Build Tool: Maven  
-Data Serialization: Jackson (for parsing JSON)  
-Database: JSON file (not a relational DB, data persists in a file)  
-Testing: JUnit, JaCoCo (for code coverage)  
-Logging: Log4j   
-API Documentation: Swagger (for API documentation and testing)  

## Installation
To run this project locally, ensure you have the following installed:    
-Java 17 or later  
-Maven 3.9.6   
Clone the repository : git clone https://github.com/Lulippe-Hiboude/SafetyNetAlerts.git

## Error Handling
The API uses standard HTTP status codes to indicate the result of an API request.  
Some common responses:  
-200 OK: The request was successful  
-400 Bad Request: The request is malformed or missing parameters  
-404 Not Found: The resource could not be found  
-500 Internal Server Error: A server-side error occurred

## Logging
The application uses Log4j for logging. Log levels are defined as follows:  
-INFO: Successful requests and responses  
-ERROR: Errors or exceptions during processing  
-DEBUG: Informative logs for troubleshooting  
Logs are stored in the console and can also be configured to be written to files.
