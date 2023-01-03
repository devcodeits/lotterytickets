CREATE TABLE lotto_model
(
    returnValue VARCHAR(20),
    drwNoDate   VARCHAR(20),
    drwNo       INTEGER PRIMARY KEY,
    drwtNo1     INTEGER,
    drwtNo2     INTEGER,
    drwtNo3     INTEGER,
    drwtNo4     INTEGER,
    drwtNo5     INTEGER,
    drwtNo6     INTEGER,
    bnusNo      INTEGER,
    drwtNos     VARCHAR(50) UNIQUE
);
CREATE TABLE lotto_arr
(
    no1 INTEGER,
    no2 INTEGER,
    no3 INTEGER,
    CONSTRAINT pk_lotto_arr PRIMARY KEY (no1, no2, no3)
);

SELECT COALESCE(MAX(drwNo), 1) as drwNo
FROM lotto_model
;
