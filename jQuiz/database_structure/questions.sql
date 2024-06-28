USE quizDB;

CREATE TABLE questions
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_type INT,
    text TEXT,
    quiz_id INT,
    image MEDIUMBLOB,
    image_url VARCHAR(100),
    score INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

INSERT INTO questions (question_type, text, quiz_id, image, image_url, score)
    VALUES (0,'Name the first US president',1,null,null,1);
INSERT INTO questions (question_type, text, quiz_id, image, image_url, score)
VALUES (0,'Name the president who wrote the declaration of independence',1,null,null,1);
INSERT INTO questions (question_type, text, quiz_id, image, image_url, score)
VALUES (4,'Who was the president during the US civil war?',1,null,null,1);

INSERT INTO questions (question_type, text, quiz_id, image, image_url, score)
VALUES (5,'Select all the presidents who were elected in the 21st century.',1,null,null,1);
