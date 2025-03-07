# Weather App

Weather App is a web application that allows users to view current weather data, historical weather data, and manage their favorite cities. The project includes features such as user registration, login, fetching weather data from an API, and secure session management with cookies.

## Screenshots

![Screenshot 2024-12-12 at 9 15 00 AM (2)](https://github.com/user-attachments/assets/9efe1ba3-d787-4dce-9f4b-5682b7e4b333)
![Screenshot 2024-12-13 at 3 07 59 PM (2)](https://github.com/user-attachments/assets/b3a31a3e-9086-4254-8f53-dd6009082f3f)
![Screenshot 2024-12-13 at 3 04 03 PM (2)](https://github.com/user-attachments/assets/d1f20db3-3cc6-4c77-9272-d91238a0e8b8)
![Screenshot 2024-12-13 at 3 42 39 PM (2)](https://github.com/user-attachments/assets/0ff68141-bc46-4d00-8885-c9aa9897f135)

## Project Structure

The project is divided into two main directories:

WeatherApp-Backend: Contains the backend code for the application.
WeatherApp-Frontend: Contains the frontend code for the application.

## WeatherApp-Backend

The backend is built using Spring Boot and provides RESTful APIs for user authentication, fetching weather data, and managing favorite cities. It includes the following key components:

User Registration and Login: Users can register and log in to the application. Passwords are securely hashed, and JWT tokens are used for session management.
Fetching Weather Data: Users can fetch current and historical weather data from an external API.
Managing Favorite Cities: Users can add, view, and delete their favorite cities.
Secure Session Management: JWT tokens are used to manage user sessions securely.

#### Directory Structure

```
WeatherApp-Backend/
├── .gitattributes
├── .gitignore
├── .mvn/
│ └── wrapper/
│ └── maven-wrapper.properties
├── bin/
│ ├── .gitattributes
│ ├── .gitignore
│ ├── .mvn/
│ │ └── wrapper/
│ ├── mvnw
│ ├── mvnw.cmd
│ ├── pom.xml
│ └── src/
│ ├── main/
│ └── test/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

### WeatherApp-Frontend

The frontend is built using React and provides a user-friendly interface for interacting with the application. It includes the following key components:

User Registration and Login: Users can register and log in to the application.
Fetching Weather Data: Users can view current and historical weather data.
Managing Favorite Cities: Users can add, view, and delete their favorite cities.
Profile Management: Users can update their profile information.

#### Directory Structure

```
WeatherApp-Frontend/
├── .gitignore
├── package.json
├── public/
│ ├── favicon.ico
│ ├── index.html
│ ├── logo192.png
│ ├── logo512.png
│ ├── manifest.json
│ ├── robots.txt
│ └── weather-app.ico
├── src/
│ ├── App/
│ ├── common/
│ ├── components/
│ │ ├── CurrentWeatherData/
│ │ ├── HistoryWeatherData/
│ │ ├── WelcomePage/
│ │ ├── LoadingIndicator/
│ │ ├── TokenExpirationPage/
│ │ └── Header/
│ ├── Context/
│ │ └── applicationContext.js
│ ├── FavouriteCities/
│ ├── user/
│ ├── util/
│ ├── App.js
│ ├── index.js
│ └── index.css
└── tailwind.config.js
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Node.js and npm
- Maven

### Backend Setup

1. Navigate to the WeatherApp-Backend directory.
2. Run the following command to build the project: `./mvnw clean install`
3. Run the following command to start the backend server: `./mvnw spring-boot:run`

### Frontend Setup

1. Navigate to the WeatherApp-Frontend directory.
2. Run the following command to install the dependencies: `npm install`
3. Run the following command to start the frontend development server: `npm start`

### Features

- User Registration: Users can register by providing their details.
- User Login: Users can log in using their credentials.
- Fetching Weather Data: Users can view current and historical weather data.
- Managing Favorite Cities: Users can add, view, and delete their favorite cities.
- Profile Management: Users can update their profile information.
- Secure Session Management: JWT tokens are used to manage user sessions securely.

### Technologies Used

- Backend: Spring Boot, Spring Security, JWT, JPA, PostgreSQL
- Frontend: React, TailwindCSS
