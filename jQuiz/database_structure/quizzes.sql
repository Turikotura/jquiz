USE quizDB;

CREATE TABLE quizzes
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    author_id INT,
    created_at DATETIME,
    time INT,
    thumbnail LONGBLOB,
    thumbnail_url VARCHAR(100),
    should_mix_up BIT,
    show_all BIT,
    auto_correct BIT,
    allow_practice BIT,
    description TEXT,
    category VARCHAR(50),
    FOREIGN KEY (author_id) REFERENCES users(id)
);
INSERT INTO quizzes (title, author_id, created_at, time, thumbnail, thumbnail_url, should_mix_up, show_all, auto_correct, allow_practice, description, category)
VALUES
    ('US Presidents Quiz', 1, '2024-06-16 13:30:17', 120, null, './database_structure/example_images/presidents.png', true, true, true, true, 'A quiz about US Presidents', 'History'),
    ('World Capitals Quiz', 2, '2024-06-18 13:30:17', 100, null, './database_structure/example_images/paris.jpeg', true, true, true, true, 'Test your knowledge of world capitals', 'Geography'),
    ('Science Trivia Quiz', 3, '2024-06-19 13:30:17', 150, null, './database_structure/example_images/science.jpeg', false, true, true, true, 'A fun quiz on various science topics', 'Science'),
    ('Historical Events Quiz', 4, sysdate(), 130, null, './database_structure/example_images/history.jpeg', true, false, true, true, 'Quiz on major historical events', 'History'),
    ('Famous Artists Quiz', 5, sysdate(), 90, null, './database_structure/example_images/ye.jpg', true, true, true, true, 'Identify famous artists by their works', 'History'),
    ('Geography Quiz', 1, sysdate(), 110, null, null, false, true, true, true, 'A quiz covering various geographical topics', 'Geography'),
    ('Movie Quotes Quiz', 2, sysdate(), 140, null, null, true, false, true, true, 'Match the quote to the movie', 'Movies'),
    ('Literature Quiz', 3, sysdate(), 160, null, null, true, true, true, true, 'Questions about famous literature works', 'Literature'),
    ('Mathematics Quiz', 4, sysdate(), 105, null, null, true, false, true, true, 'Test your math skills with this quiz', 'Science'),
    ('Sports Trivia Quiz', 5, sysdate(), 125, null, null, true, true, true, true, 'Trivia questions about various sports', 'Sports'),
