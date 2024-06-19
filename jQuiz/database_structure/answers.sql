USE quizDB;

CREATE TABLE answers
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    text TEXT,
    is_correct BOOL,
    question_id INT,
    uniq_id INT,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('washington',true,3,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('george washington',true,3,1);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('jefferson',true,4,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('thomas jefferson',true,4,1);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Abraham Lincoln',true,5,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('thomas jefferson',false,5,2);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Ulysses S. Grant',false,5,3);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Theodore Roosevelt',false,5,4);

INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Barack Obama',true,6,1);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Ronald Reagan',false,6,2);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('Bill Clinton',false,6,3);
INSERT INTO answers (text,is_correct,question_id,uniq_id)
VALUES ('George W. Bush',true,6,4);

INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('George Washington', true, 1, 1);
INSERT INTO answers (text, is_correct, question_id, uniq_id)
VALUES ('John Adams', true, 1, 2);

# INSERT INTO answers (text, is_correct, question_id, uniq_id)
# VALUES ('Africa', true, 2, 1);
# INSERT INTO answers (text, is_correct, question_id, uniq_id)
# VALUES ('Europe', true, 2, 2);