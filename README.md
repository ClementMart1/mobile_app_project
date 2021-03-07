# mobile_app_project
Clément MARTIN - IOS 1 - S8

- Explain how you ensure user is the right one starting the app

I've put a login activity where the user has to enter. I then check if the name and lastname are in the database on the API. If not,
it stays on the screen with a Toast message.

- How do you securely save user's data on your phone ?

I use SQLite to store the data of the users. I then use SQLCipher to encrypt the database.

- How did you hide the API url ?

I obfuscate all of my code using ProGuard. The urls are obfuscated. I have a keystore file on my hardware where there's the private key

