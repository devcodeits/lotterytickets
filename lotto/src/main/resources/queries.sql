-- data length
SELECT COUNT(*)
FROM lotto_arr
;
-- pick numbers by data
SELECT *
FROM (SELECT row_number() over (order by RANDOM()) AS rnum, *
      FROM lotto_arr) a
WHERE rnum = (SELECT trunc(random() * 6258 + 1) FROM generate_series(1, 6258) LIMIT 1)
;
-- pick numbers by random
SELECT *
FROM (SELECT trunc(random() * 45 + 1),
             trunc(random() * 45 + 1),
             trunc(random() * 45 + 1)
      FROM generate_series(1, 6258)) a
ORDER BY random()
LIMIT 1
;
-- pick numbers by complex
SELECT *,
       (SELECT *
        FROM (SELECT trunc(random() * 45 + 1) FROM generate_series(1, 6258)) a
        ORDER BY random()
        LIMIT 1) AS no4,
       (SELECT *
        FROM (SELECT trunc(random() * 45 + 1) FROM generate_series(1, 6258)) a
        ORDER BY random()
        LIMIT 1) AS no5,
       (SELECT *
        FROM (SELECT trunc(random() * 45 + 1) FROM generate_series(1, 6258)) a
        ORDER BY random()
        LIMIT 1) AS no6
FROM (SELECT row_number() over (order by RANDOM()) AS rnum, *
      FROM lotto_arr) a
WHERE rnum = (SELECT trunc(random() * 6258 + 1) FROM generate_series(1, 6258) LIMIT 1)
;
