USE quizDB;

CREATE TABLE tagToQuiz (
    quiz_id INT,
    tag_id INT,
    PRIMARY KEY (quiz_id, tag_id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);


INSERT INTO tagToQuiz (quiz_id, tag_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (3, 5),
    (3, 6),
    (3, 7),
    (3, 8),
    (2, 9),
    (2, 10),
    (2, 11),
    (4, 12),
    (4, 13),
    (4, 14),
    (5, 15),
    (5, 16),
    (5, 17),
    (6, 9),
    (6, 10),
    (7, 18),
    (7, 19),
    (8, 19),
    (8, 20),
    (9, 21),
    (9, 22),
    (10, 23),
    (10, 24),
    (10, 25),
    (10, 26);

