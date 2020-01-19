# Probability Prediction of Road Accidents by Machine Learning(2020)
## Team 4 - Road Traffic Accident Prediction Web Application



### Description of the project
In this project, we set out to predict the probability of Road Traffic Accidents, by providing a tool to predict RTA risk so that users can make informed decision about their traveling route. We also did a detailed analysis of past data and visuals to gain a better understanding of RTA.

Our web interface contains two parts, Information about web app and interaction. In the exploration part, we presents our research methodology, algorithm used. In the interaction part, user can make use of an interactive dashboard to predict the probability of RTA in their chosen routes.

The app uses machine learning models for predictions. User will have to enter a date and time, an origin latitude, latitude and travel destination latitude, longitude. The app will then call [Here maps Routing API]/) for route planning and a weather API, [Darksky](https://darksky.net/dev)
for weather forecasts. These data will then be fed into the model and probability of RTA occurring in user's route will be displayed on the map as hazard icons.

The web application is built using Python Flask framework.

---
### Installation and setup

**a. First-time setup**  
1) Clone The directory  

2) Make sure you are connected to the internet

3) The Python virtual environment for this project is created using [Pipenv](https://pipenv.readthedocs.io/en/latest/), which is a Python package. First, install the package to your local Python program if you don't have the package. Open a new command line,
 ```
 pip install pipenv
 ```

4) To install project package requirements from *Pipfile* and *Pipfile.lock* in root folder:  

   ```
   cd your_project_directory
   ```

   - Create a virtual environment for this project.
   ```
   pipenv shell
   ```

   - Install all the packages from the *Pipfile* into the virtual environment.
   ```
   pipenv install
   ```
   - All the packages required will now be installed.  

&nbsp; 

***You have to change api keys in config.py in /app folder   

**b. To run the Flask app locally:**  
1) Navigate to the project folder in command prompt, run the following to activate the virtual environment.
  ```
  pipenv shell
  ```
2) Running flask,
   - In Windows command prompt, the following command will  export the Flask application and run the app in debug mode.
   ```
   set FLASK_DEBUG=1
   ```  
   ```
   flask run
   ```   

3) Ensure that you have internet access. Open the localhost in your Google Chrome, 'http://localhost:5000/'  
