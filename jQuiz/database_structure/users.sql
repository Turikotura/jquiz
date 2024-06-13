USE quizDB;

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id INT PRIMARY KEY,
    username VARCHAR(50),
    is_admin BOOL,
    created_at DATETIME,
    email VARCHAR(50),
    pass VARCHAR(50)
);