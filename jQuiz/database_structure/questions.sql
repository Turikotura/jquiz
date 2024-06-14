USE quizDB;

CREATE TABLE questions
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_type_id INT,
    text TEXT,
    quiz_id INT,
    image_url VARCHAR(100),
    score INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

INSERT INTO questions (question_type_id, text, quiz_id, image_url, score)
    VALUES (1, 'Name US presidents', 2, NULL, 1)
INSERT INTO questions (question_type_id, text, quiz_id, image_url, score)
    VALUES (2, 'Name world continents', 2, NULL, 1)