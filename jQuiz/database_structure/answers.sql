USE quizDB;

CREATE TABLE answers
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    text TEXT,
    is_correct BOOL,
    question_id INT,
    uniq_id INT,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);


INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('George Washington', true, 1, 1)
INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('John Adams', true, 1, 2)

INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('Africa', true, 2, 1)
INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('Europe', true, 2, 2)