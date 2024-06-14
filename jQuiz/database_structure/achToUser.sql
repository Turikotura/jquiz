USE quizDB;

CREATE TABLE achToUser
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ach_id INT,
    aquire_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ach_id) REFERENCES achievements(id)
);