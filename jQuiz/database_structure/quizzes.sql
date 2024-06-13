USE quizDB;

DROP TABLE IF EXISTS quizzes;

CREATE TABLE quizzes
(
    id INT PRIMARY KEY,
    title VARCHAR(50),
    author_id INT,
    created_at DATETIME,
    time INT,
    thumbnail VARCHAR(100),
    FOREIGN KEY (author_id) REFERENCES users(id)
);