USE quizDB;

CREATE TABLE answers
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    text TEXT,
    is_correct BOOL,
    question_id INT,
    uniq_id INT,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('washington',true,1,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('george washington',true,1,1);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('jefferson',true,2,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('thomas jefferson',true,2,1);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Abraham Lincoln',true,3,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Thomas Jefferson',false,3,2);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Ulysses S. Grant',false,3,3);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Theodore Roosevelt',false,3,4);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Barack Obama',true,4,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Ronald Reagan',false,4,2);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Bill Clinton',false,4,3);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('George W. Bush',true,4,4);




-- Answers for question 1: What is the capital of France?
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Paris', true, 5, 1);

-- Answers for question 2: The capital of Japan is ____.
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Harare', true, 6, 1);

-- Answers for question 3: Identify the capital city from the picture
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Tokyo', true, 7, 1);

-- Answers for question 4: Select all the capitals in South America.
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Buenos Aires', true, 8, 1),
('Brasília', true, 8, 2),
('Washington D.C.', false, 8, 3),
('Santiago', true, 8, 4),
('Bogotá', true, 8, 5),
('Caracas', true, 8, 6),
('Lima', true, 8, 7),
('London', false, 8, 8),
('Montevideo', true, 8, 9),
('Quito', true, 8, 10),
('San Jose', false, 8, 11),
('Sucre', true, 8, 12),
('Asunción', true, 8, 13),
('Tokyo', false, 8, 14);

-- Answers for question 5: What is the capital of Canada?
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Ottawa', true, 9, 1);

-- Answers for question 6: List the capitals of the following countries: Australia, Brazil, and China.
INSERT INTO answers (text, is_correct, question_id, uniq_id) VALUES
('Canberra', true, 10, 1),
('Brasília', true, 10, 2),
('Beijing', true, 10, 3);
