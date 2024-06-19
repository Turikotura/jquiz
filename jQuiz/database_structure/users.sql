USE quizDB;

CREATE TABLE users
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    is_admin BOOL,
    created_at DATETIME,
    email VARCHAR(50) UNIQUE,
    pass VARCHAR(50),
    image VARCHAR(100)
);
INSERT INTO users (id, username, is_admin, created_at, email, pass, image) VALUES
(1, 'luka', 1, '2024-06-16 13:30:17', 'luka@example.com', '123', 'tmpimg'),
(2, 'john', 0, '2024-06-16 13:35:17', 'john@example.com', '123', 'tmpimg2'),
(3, 'sara', 0, '2024-06-16 13:40:17', 'sara@example.com', '123', 'tmpimg3'),
(4, 'mike', 0, '2024-06-16 13:45:17', 'mike@example.com', '123', 'tmpimg4'),
(5, 'anna', 0, '2024-06-16 13:50:17', 'anna@example.com', '123', 'tmpimg5');