USE quizDB;

DROP TABLE IF EXISTS achievements;

CREATE TABLE achievements
(
    id INT PRIMARY KEY,
    name VARCHAR(50),
    description TEXT,
    image VARCHAR(100)
);