USE quizDB;

CREATE TABLE comments
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    text TEXT,
    written_time DATETIME,
    user_id INT,
    quiz_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);