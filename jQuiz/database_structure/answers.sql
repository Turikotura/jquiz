USE quizDB;

DROP TABLE IF EXISTS answers;

CREATE TABLE answers
(
    answer_id INT PRIMARY KEY,
    answer_text TEXT,
    is_correct BOOL,
    question_id INT,
    uniq_id INT,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);