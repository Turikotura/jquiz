# JQuiz

This is our final project for OOP class.

## Features

1. Quiz creation
2. Information display
3. Quiz play logic with practice mode
4. Quiz display by various methods
5. Search and filtration system
6. Mail system
7. Friend system with activity
8. Administration
9. Categories and tags
10. Achievements
11. History
12. Login/Registration
13. Image upload
14. Statistics
15. User ban
16. Protection from SQL injections
17. Quiz comments and rating
18. Announcements
19. Report system

## Basic overview of the system

* database - Where the program can communicate with SQL tables
* accounts - system used for registration/login
* attempts - system used for playing quizzes
* statistics - system used for getting statistics
* listeners - bind the databases and various systems for website use
* servlets - controllers for website inputs

## Pages

1. achievements - Displays achievements gotten by the user
2. categories - Displays the various quiz categories(Are predefined and can't be changed)
3. category - A single category page showing quizzes in that category
4. createQuiz - Page used for creating quizzes
5. friends - Displays friends of the currently logged in user
6. header - Header of the webpage(Displayed in every page)
7. historySummary - Displays every history of the quizzes the user has written
8. index - Home page
9. login - Page used to log into the site
10. mail - Displays the mail panel(Displayed in every page)
11. playQuiz - Page used for playing quizzes
12. profile - Displays information about users
13. quizInfo - Displays information about a quiz
14. quizResult - Displays information about the finished quiz attempt
15. quizzes - Page used to display every quiz with filtration(see all)
16. register - Displays user registration page
17. search - Page displayed after search bar input
18. statistics - Page displaying the statistics of the quiz site

## For testing

Before testing, run  'run.sh' to set up database structure and change DBInfo credentials
Base administrator is System, pass : xyz