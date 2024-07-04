USE quizDB;

CREATE TABLE tags (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO tags (name)
VALUES

    ('usa'),
    ('presidents'),
    ('washington'),
    ('world_leaders'),
    ('chemistry'),
    ('physics'),
    ('biology'),
    ('science'),
    ('cities'),
    ('geography'),
    ('capitals'),
    ('events'),
    ('roman_empire'),
    ('ataturk'),
    ('van_gogh'),
    ('picasso'),
    ('renaissance'),
    ('cinema'),
    ('famous_quotes'),
    ('books'),
    ('math'),
    ('mathematics'),
    ('nba'),
    ('football'),
    ('basketball'),
    ('sports');


