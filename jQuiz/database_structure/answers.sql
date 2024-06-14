USE quizDB;

DROP TABLE IF EXISTS answers;

CREATE TABLE answers
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    text TEXT,
    is_correct BOOL,
    question_id INT,
    uniq_id INT,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);