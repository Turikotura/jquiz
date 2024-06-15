USE quizDB;

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


INSERT INTO quizzes (title, author_id, created_at, time, thumbnail, should_mix_up,
                   show_all, auto_correct, allow_practice, description)
           VALUES ('World Continent Quiz', 1, sysdate(), 60, 'tmpimg', true, true, true, true, 'World continent knowledge quiz');

INSERT INTO quizzes (title, author_id, created_at, time, thumbnail, should_mix_up,
                   show_all, auto_correct, allow_practice, description)
VALUES ('US Presidents Quiz', 1, sysdate(), 120, 'tmpimg', true, true, true, true, 'US Presidents Quiz');

SELECT *
FROM quizzes
ORDER BY (SELECT )