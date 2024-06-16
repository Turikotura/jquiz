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

SELECT a.id, a.name, a.description, a.image,
       au.user_id, au.acquire_date
FROM achievements a
         JOIN achToUser au ON a.id = au.ach_id
WHERE au.user_id = 2;  -- Replace with the specific user_id you're searching for