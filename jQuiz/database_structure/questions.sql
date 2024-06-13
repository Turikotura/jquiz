USE quizDB;

DROP TABLE IF EXISTS questions;

CREATE TABLE questions
(
    id INT PRIMARY KEY,
    question_type_id INT,
    text TEXT,
    quiz_id INT,
    image_url VARCHAR(100),
    score INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);