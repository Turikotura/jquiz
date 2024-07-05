USE quizDB;

CREATE TABLE ratings
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    rating INT,
    quiz_id INT,
    user_id INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK ( rating >= 0 ),
    CHECK ( rating <= 5 )
);

ALTER table ratings add check (rating >= 0);
alter table ratings add check ( rating <= 5 );