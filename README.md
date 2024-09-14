# Smart Homes Project

This project is a full-stack web application with separate frontend and backend components.

- The **Frontend** is built using **React**.
- The **Backend** is built using **Java Servlets** and deployed on **Tomcat**.

## Table of Contents

1. [Available Scripts for Frontend](#available-scripts-for-frontend)
2. [Frontend Setup](#frontend-setup)
3. [Backend Setup](#backend-setup)
4. [Running the Application](#running-the-application)
5. [Learn More](#learn-more)

## Available Scripts for Frontend

In the project directory `frontend`, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point, you're on your own.

You don't have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn't feel obligated to use this feature. However, we understand that this tool wouldn't be useful if you couldn't customize it when you are ready for it.

## Frontend Setup

### Steps to run the React frontend:

1. Clone the repository:

   ```bash
   git clone https://github.com/dhruvpatel77741/csp584-ewa.git
   cd csp584-ewa
   ```

2. Navigate to the frontend folder:
   cd frontend

3. Install the dependencies:
   npm install

4. Setup env file in frontend folder named .env
     Inside it enter: REACT_APP_API_BASE_URL="Your Backend API Base URL"
   
5. Note about API Base URL:
   Currently, the API base URL is not included since this is a university project. If needed, update the API endpoint in the frontend code to connect with the backend.

6. Run the React app:
   npm start

## Backend Setup

### Steps to run the Java Servlet backend using Tomcat:

1. **Download and install Apache Tomcat 9.0.93:**
   You can download Tomcat from [this link](https://tomcat.apache.org/download-90.cgi).

2. **Deploy the Java Servlet app (csp584):**

   - After cloning the project, copy the `csp584` folder (located inside the `backend` folder) to the `webapps` directory of your local Tomcat installation.
   - Your directory structure should look something like this:
     tomcat/ ├── bin/ ├── webapps/ │ └── csp584/

3. **Start Tomcat:**

- For **Windows**, run `startup.bat` located in the `bin` folder of Tomcat.
- For **Mac/Linux**, run the following command:
  ```bash
  ./startup.sh
  ```

4. **Access the backend:**
   By default, Tomcat runs on `http://localhost:8080`. You can access the backend API at:
   "The backend URL will be provided later or needs to be configured manually based on your setup."

### Notes for Backend:

- Ensure that the **Java Development Kit 11 (JDK)** is installed and configured.
- Update any configuration files, such as `web.xml`, to match your project’s requirements.

## Running the Application

1. **Start the Frontend**: Follow the steps in the [Frontend Setup](#frontend-setup) section to start the React frontend.
2. **Start the Backend**: Follow the steps in the [Backend Setup](#backend-setup) section to start the Java Servlet backend using Tomcat.

Ensure both the frontend and backend are running concurrently. The frontend will communicate with the backend through the API endpoints.
