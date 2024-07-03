USE quizDB;

CREATE TABLE friends
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user1_id INT,
    user2_id INT,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id)
);

SELECT *
FROM users
where id IN (SELECT user2_id
             from friends
             where user1_id = 6)