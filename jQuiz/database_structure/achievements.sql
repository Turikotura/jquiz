USE quizDB;

CREATE TABLE achievements
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description TEXT,
    image VARCHAR(100)
);

INSERT INTO achievements (name, description, image) VALUES ('Amateur Author', 'Created a quiz.', 'https://cdn-icons-png.freepik.com/256/6938/6938538.png?semt=ais_hybrid');
INSERT INTO achievements (name, description, image) VALUES ('Prolific Author', 'Created 5 quizzes.', 'https://cdn-icons-png.freepik.com/256/4803/4803688.png?semt=ais_hybrid');
INSERT INTO achievements (name, description, image) VALUES ('Prodigious Author', 'Created 10 quizzes.', 'https://cdn-icons-png.freepik.com/256/2178/2178197.png?semt=ais_hybrid');
INSERT INTO achievements (name, description, image) VALUES ('Quiz Machine', 'Took 10 quizzes.', 'https://cdn-icons-png.freepik.com/256/2450/2450980.png?semt=ais_hybrid');
INSERT INTO achievements (name, description, image) VALUES ('I am the Greatest', 'Scored highest on a quiz.', 'https://cdn-icons-png.freepik.com/256/13145/13145875.png?semt=ais_hybrid');
INSERT INTO achievements (name, description, image) VALUES ('Practice Makes Perfect', 'Took a quiz in practice mode.', 'https://cdn-icons-png.freepik.com/256/10822/10822455.png?semt=ais_hybrid');