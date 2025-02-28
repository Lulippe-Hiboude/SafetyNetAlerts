# SafetyNetAlerts
The SafetyNet Alerts application is designed to send critical information to emergency services systems.

This backend API serves as a communication bridge between the application and the emergency service teams. It allows retrieving, updating, and deleting data regarding citizens, their medical records, fire stations, and alerts for various emergencies.

The API offers endpoints to retrieve personal and medical data, alert specific services based on fire station coverage, and send notifications in case of an emergency. Additionally, it provides data about all citizens in a given city, facilitating the quick dissemination of vital information to first responders.

# Technologies Used
*Backend Framework: Spring Boot  
*Build Tool: Maven  
*Data Serialization: Jackson (for parsing JSON)  
*Database: JSON file (not a relational DB, data persists in a file)  
*Testing: JUnit, JaCoCo (for code coverage)  
*Logging: Log4j   
*API Documentation: Swagger (for API documentation and testing)  

# Installation
To run this project locally, ensure you have the following installed
*Java 17 or later
*Maven 3.9.6 
Clone the repository : git clone 
