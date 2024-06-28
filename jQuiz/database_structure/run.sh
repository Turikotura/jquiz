#!/bin/bash

DB_USER="root"
DB_PASSWORD="giorgiPass1!"
DB_NAME="quizDB"  # Define your database name here
echo

SQL_DIR="."

mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/create_database.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/drop.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/users.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/quizzes.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/questions.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/answers.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/question_types.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/achievements.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/achToUser.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/friends.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/history.sql
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/mails.sql

mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $SQL_DIR/quizzes_view.sql
