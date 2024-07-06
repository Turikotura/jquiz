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


-- Inserting multiple questions for the World Capitals quiz in a single INSERT command
INSERT INTO questions (question_type, text, quiz_id, image, image_url, score) VALUES
(0, 'What is the capital of France?', 2, null, null, 1),
(2, 'The capital of Zimbabwe is {}.', 2, null, null, 1),
(3, 'Identify the capital city from the picture', 2, null, './database_structure/example_images/tokyo.jpeg', 1),
(5, 'Select all the capitals in South America.', 2, null, null, 1),
(0, 'What is the capital of Canada?', 2, null, null, 1),
(1, 'List the capitals of the following countries: Australia, Brazil, and China.', 2, null, null, 1);

