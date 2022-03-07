CREATE TYPE game_state AS ENUM ('IN_PROGRESS', 'PLAYER_WIN', 'COMPETITOR_WIN');
CREATE CAST (varchar AS game_state) WITH INOUT AS IMPLICIT;

CREATE TYPE game_result AS ENUM ('INTERRUPTED_BY_PLAYER', 'PLAYER_WIN', 'COMPETITOR_WIN');
CREATE CAST (varchar AS game_result) WITH INOUT AS IMPLICIT;

CREATE TYPE move AS ENUM ('ROCK', 'PAPER', 'SCISSORS');
CREATE CAST (varchar AS move) WITH INOUT AS IMPLICIT;

CREATE TABLE users (
   id bigserial PRIMARY KEY
);

CREATE TABLE games (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(id),
    state game_state NOT NULL DEFAULT 'IN_PROGRESS',
    result game_result DEFAULT NULL
);

CREATE TABLE moves (
   game_id bigint REFERENCES games(id),
   player_move move NOT NULL,
   competitor_move move NOT NULL,
   number int NOT NULL
);
CREATE UNIQUE INDEX moves_unique ON moves (game_id, number);