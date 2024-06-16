USE quizDB;

CREATE TABLE quizzes
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50),
    author_id INT,
    created_at DATETIME,
    time INT,
    thumbnail VARCHAR(100),
    should_mix_up BIT,
    show_all BIT,
    auto_correct BIT,
    allow_practice BIT,
    description TEXT,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

INSERT INTO quizzes (title, author_id, created_at, time, thumbnail, should_mix_up, show_all, auto_correct, allow_practice, description)
VALUES
    ('US Presidents Quiz', 1, sysdate(), 120, 'tmpimg1', true, true, true, true, 'A quiz about US Presidents'),

    ('World Capitals Quiz', 2, sysdate(), 100, 'tmpimg2', true, true, true, true, 'Test your knowledge of world capitals'),

    ('Science Trivia Quiz', 3, sysdate(), 150, 'tmpimg3', false, true, true, true, 'A fun quiz on various science topics'),

    ('Historical Events Quiz', 4, sysdate(), 130, 'tmpimg4', true, false, true, true, 'Quiz on major historical events'),

    ('Famous Artists Quiz', 5, sysdate(), 90, 'tmpimg5', true, true, true, true, 'Identify famous artists by their works'),

    ('Geography Quiz', 1, sysdate(), 110, 'tmpimg6', false, true, true, true, 'A quiz covering various geographical topics'),

    ('Movie Quotes Quiz', 2, sysdate(), 140, 'tmpimg7', true, false, true, true, 'Match the quote to the movie'),

    ('Literature Quiz', 3, sysdate(), 160, 'tmpimg8', true, true, true, true, 'Questions about famous literature works'),

    ('Mathematics Quiz', 4, sysdate(), 105, 'tmpimg9', true, false, true, true, 'Test your math skills with this quiz'),

    ('Sports Trivia Quiz', 5, sysdate(), 125, 'tmpimg10', true, true, true, true, 'Trivia questions about various sports');
