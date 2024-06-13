USE quizDB;

DROP TABLE IF EXISTS mails;

CREATE TABLE mails
(
    id INT PRIMARY KEY,
    sender_id INT,
    receiver_id INT,
    type INT,
    quiz_id INT,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);