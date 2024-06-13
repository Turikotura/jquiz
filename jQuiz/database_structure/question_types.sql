USE quizDB;

DROP TABLE IF EXISTS question_types;

CREATE TABLE question_types
(
    id INT PRIMARY KEY,
    type_name VARCHAR(50)
);

DROP TABLE question_types;