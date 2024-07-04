USE quizDB;

CREATE TABLE users
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    is_admin BOOL,
    created_at DATETIME,
    email VARCHAR(50) UNIQUE,
    pass VARCHAR(50),
    image MEDIUMBLOB,
    image_url VARCHAR(100)
);
INSERT INTO users (username, is_admin, created_at, email, pass, image, image_url) VALUES
('System',1,sysdate(),'adminMail@gmail.com','66b27417d37e024c46526c2f6d358a754fc552f3',null,''),
('luka', 1, '2024-06-16 13:30:17', 'luka@example.com', '70ccd9007338d6d81dd3b6271621b9cf9a97ea00', null, './database_structure/example_images/luka.jpg'),
('ye', 0, '2024-06-16 13:35:17', 'ye@example.com', '123', null, './database_structure/example_images/ye.jpg'),
('ana', 0, '2024-06-16 13:40:17', 'ana@example.com', '123', null, './database_structure/example_images/ana.jpeg'),
('anon', 0, '2024-06-16 13:45:17', 'mike@example.com', '123', null, ''),
('jackie', 0, '2024-06-16 13:50:17', 'anna@example.com', '123', null, './database_structure/example_images/jackie.jpeg');
