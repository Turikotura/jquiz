USE quizDB;

DROP TABLE IF EXISTS friends;

CREATE TABLE friends
(
    id INT PRIMARY KEY,
    user1_id INT,
    user2_id INT,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id)
);