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

SELECT q.id, q.title, q.author_id, q.created_at, q.time, q.thumbnail,
       q.should_mix_up, q.show_all, q.auto_correct, q.allow_practice, q.description,
       COUNT(h.quiz_id) AS quiz_count
FROM quizzes q
         LEFT JOIN history h ON q.id = h.quiz_id
GROUP BY q.id, q.title, q.author_id, q.created_at, q.time, q.thumbnail,
         q.should_mix_up, q.show_all, q.auto_correct, q.allow_practice, q.description
ORDER BY quiz_count DESC;

SELECT *
FROM quizzes
ORDER BY created_at DESC
