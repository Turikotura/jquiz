USE quizDB;

CREATE TABLE achievements
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description TEXT,
    image VARCHAR(100)
);

INSERT INTO achievements (name, description, image) VALUES ('Most quizzes', 'Has written the most quizzes', 'tmp.jpg');
INSERT INTO achievements (name, description, image) VALUES ('Least quizzes', 'Has written the least quizzes', 'tmp.jpg');
INSERT INTO achievements (name, description, image) VALUES ('Biggest quizzes', 'Has written the biggest quizzes', 'tmp.jpg');