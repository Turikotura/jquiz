USE quizDB;

CREATE TABLE questions
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_type INT,
    text TEXT,
    quiz_id INT,
    image MEDIUMBLOB,
    score INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

INSERT INTO questions (question_type, text, quiz_id, image, score)
    VALUES (0,'Name the first US president',1,null,1);
INSERT INTO questions (question_type, text, quiz_id, image, score)
VALUES (0,'Name the president who wrote the declaration of independence',1,null,1);
INSERT INTO questions (question_type, text, quiz_id, image, score)
VALUES (4,'Who was the president during the US civil war?',1,null,1);

INSERT INTO questions (question_type, text, quiz_id, image, score)
VALUES (5,'Select all the presidents who were elected in the 21st century.',1,null,1);

# INSERT INTO questions (question_type, text, quiz_id, image, score)
#     VALUES (1, 'Name US presidents', 2, null, 1);
# INSERT INTO questions (question_type, text, quiz_id, image, score)
#     VALUES (2, 'Name world continents', 2, null, 1);
