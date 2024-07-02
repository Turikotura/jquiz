USE quizDB;

CREATE TABLE achToUser
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ach_id INT,
    acquire_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ach_id) REFERENCES achievements(id)
);

# INSERT INTO achToUser (user_id, ach_id, acquire_date) VALUES (1, 1, sysdate());
# INSERT INTO achToUser (user_id, ach_id, acquire_date) VALUES (1, 2, sysdate());
# INSERT INTO achToUser (user_id, ach_id, acquire_date) VALUES (2, 2, sysdate());
# INSERT INTO achToUser (user_id, ach_id, acquire_date) VALUES (2, 3, sysdate());
# INSERT INTO achToUser (user_id, ach_id, acquire_date) VALUES (3, 1, sysdate());