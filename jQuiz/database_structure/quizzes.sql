USE quizDB;

CREATE TABLE quizzes
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    author_id INT,
    created_at DATETIME,
    time INT,
    thumbnail LONGBLOB,
    should_mix_up BIT,
    show_all BIT,
    auto_correct BIT,
    allow_practice BIT,
    description TEXT,
    FOREIGN KEY (author_id) REFERENCES users(id)
);
INSERT INTO quizzes (title, author_id, created_at, time, thumbnail, should_mix_up, show_all, auto_correct, allow_practice, description)
VALUES
    ('US Presidents Quiz', 1, '2024-06-16 13:30:17', 120, null, true, true, true, true, 'A quiz about US Presidents'),
    ('World Capitals Quiz', 2, '2024-06-18 13:30:17', 100, null, true, true, true, true, 'Test your knowledge of world capitals'),
    ('Science Trivia Quiz', 3, '2024-06-19 13:30:17', 150, null, false, true, true, true, 'A fun quiz on various science topics'),
    ('Historical Events Quiz', 4, sysdate(), 130, null, true, false, true, true, 'Quiz on major historical events'),
    ('Famous Artists Quiz', 5, sysdate(), 90, null, true, true, true, true, 'Identify famous artists by their works'),
    ('Geography Quiz', 1, sysdate(), 110, null, false, true, true, true, 'A quiz covering various geographical topics'),
    ('Movie Quotes Quiz', 2, sysdate(), 140, null, true, false, true, true, 'Match the quote to the movie'),
    ('Literature Quiz', 3, sysdate(), 160, null, true, true, true, true, 'Questions about famous literature works'),
    ('Mathematics Quiz', 4, sysdate(), 105, null, true, false, true, true, 'Test your math skills with this quiz'),
    ('Sports Trivia Quiz', 5, sysdate(), 125, null, true, true, true, true, 'Trivia questions about various sports');