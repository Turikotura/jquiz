USE quizDB;

CREATE TABLE mails
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT,
    receiver_id INT,
    type INT,
    quiz_id INT,
    text TEXT,
    time_sent DATETIME,
    seen BIT,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

ALTER TABLE mails
DROP CONSTRAINT mails_ibfk_3