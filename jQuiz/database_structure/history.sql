USE quizDB;

CREATE TABLE history
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    quiz_id INT,
    grade INT,
    completed_at DATETIME,
    writing_time INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);