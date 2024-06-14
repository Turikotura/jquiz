USE quizDB;

CREATE TABLE users
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    is_admin BOOL,
    created_at DATETIME,
    email VARCHAR(50),
    pass VARCHAR(50),
    image VARCHAR(100)
);

INSERT INTO users (username, is_admin, created_at, email, pass, image) VALUES ('luka', true, sysdate(), 'lt', '123', 'tmpimg');
INSERT INTO users (username, is_admin, created_at, email, pass, image) VALUES ('dachi', true, sysdate(), 'ds', '113', 'tmpimg1');
INSERT INTO users (username, is_admin, created_at, email, pass, image) VALUES ('gio', true, sysdate(), 'ge', '1234', 'tmp1img');

SELECT * FROM users where id = 1;

UPDATE users SET username = 'Tura' Where id = 1