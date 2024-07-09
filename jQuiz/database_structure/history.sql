USE quizDB;

CREATE TABLE history
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    quiz_id INT,
    grade INT,
    completed_at DATETIME,
    writing_time INT,
    is_practice BIT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

INSERT INTO history (user_id, quiz_id, grade, completed_at, writing_time) VALUES (3, 1, 10, sysdate(), 10000);
INSERT INTO history (user_id, quiz_id, grade, completed_at, writing_time) VALUES (3, 2, 10, sysdate(), 11000);
INSERT INTO history (user_id, quiz_id, grade, completed_at, writing_time) VALUES (3, 1, 10, sysdate(), 12000);
INSERT INTO history (user_id, quiz_id, grade, completed_at, writing_time) VALUES (2, 2, 20, sysdate(), 12000);
INSERT INTO history (user_id, quiz_id, grade, completed_at, writing_time) VALUES (1, 2, 20, sysdate(), 10000);

Select *
from history h
where h.quiz_id = 1
and h.grade = (Select max(hi.grade)
               from history hi
               where hi.quiz_id = 1)
order by writing_time;

SELECT *
FROM history
WHERE user_id = 3   -- Replace with the actual userId value
  AND quiz_id = 1   -- Replace with the actual quizId value
ORDER BY grade DESC, writing_time
LIMIT 1;