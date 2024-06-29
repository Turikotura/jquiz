USE quizDB;

CREATE VIEW quizzes_view AS
SELECT
    q.id,
    q.title,
    q.author_id,
    q.created_at,
    q.time,
    q.thumbnail,
    q.thumbnail_url,
    q.should_mix_up,
    q.show_all,
    q.auto_correct,
    q.allow_practice,
    q.description,
    COUNT(h.id) AS total_play_count,
    SUM(CASE WHEN h.completed_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH) THEN 1 ELSE 0 END) AS last_month_play_count
FROM
    quizzes q
        LEFT JOIN
    history h ON q.id = h.quiz_id
GROUP BY
    q.id, q.title;
