USE quizDB;

CREATE TABLE announcements
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    title      VARCHAR(50),
    author_id  INT,
    created_at DATETIME,
    text TEXT,
    FOREIGN KEY (author_id) REFERENCES users(id)
);