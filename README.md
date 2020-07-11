![MoCook Logo](/../screenshots/images/Logo.png?raw=true)
# MoCook

Android application developed in Java using the Android Studio IDE.

This application aims to enable users to exchange recipes with friends and to easily have a personalized list of recipes that may have caught their attention.

## 🛠 Architecture
In order to achieve the objective, the application is composed of the following activities:

- **Welcome activity:** Introductory activity by means of which the user is welcomed. It shows the logo of the application as well as a welcome text. In addition, an animation is used to link the present activity with the subsequent activity.

- **Login activity:** Activity through which users can access the application or, if they are not yet registered in the system, access the corresponding registration activity. To be able to log in, users will have two mechanisms managed by Firebase: users will be able of logging in using a Google account or logging in using the email/password combination.

- **User registration activity:** Through this activity users will be able to register in the app in case they do not have or do not want to make use of their Google account. In order to carry out this process, users must correctly fill in all the fields associated with the name, email and password. Some of the restrictions raised when filling out the form are the following:
    - The password must contain at least eight characters and consist of at least one uppercase, one lowercase and one numeric digit.
    - The email must have a valid format.

 - **Main activity:**  Activity in which the main functionalities of the application are gathered. Through this activity, users will be able to visualize all the recipes stored in the system and even perform a search on them. These searches can be performed by the name of the recipe, by a specific ingredient or by a specific cooking process.
 
    In addition, users will be able to quickly consult the recipes added to their list and even modify the data linked to their user account. The modification of the user account is only possible if the user has logged in with an account associated with an email and password. If the user is logged in with a Google account, users will be informed that the changes have to be made directly on their Google profile. In addition, users will also be able to view recipes created by themselves.

    If the user clicks on the floating button located on the navigation bar, the option of creating a new recipe will be visualized. This activity will also serve to edit the recipe if desired in the future.

- **Recipe activity:** Through this activity users will be able to visualize all the details associated to a recipe. Besides, users will be able to add their favourite recipes to their list by clicking on the heart located on the upper right side or share it through the adjacent button.

    In addition, if the user is the creator of the recipe, two buttons will be displayed at the bottom of the screen to edit or delete the recipe.

In order to store all the information related to the app and obtain analytics of the most used functions, Firebase services are used and implemented.

## 📱 Screenshots

![Screenshots](/../screenshots/images/Screenshots.png?raw=true)

## ℹ️ Information

Project developed for the subject Mobility and Ubiquitous Computing.
