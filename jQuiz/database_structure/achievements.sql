USE quizDB;

CREATE TABLE achievements
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description TEXT,
    image VARCHAR(100),
    unlock_date DATETIME
);