USE quizDB;

DROP TABLE IF EXISTS quizzes;

CREATE TABLE quizzes
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    author_id INT,
    created_at DATETIME,
    time INT,
    thumbnail VARCHAR(100),
    should_mix_up BIT,
    show_all BIT,
    auto_correct BIT,
    allow_practice BIT,
    description TEXT,
    FOREIGN KEY (author_id) REFERENCES users(id)
);