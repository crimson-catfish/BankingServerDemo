create table if not exists users
(
    id       int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login    text    not null,
    password text    not null,
    balance  numeric not null,
    PRIMARY KEY (ID)
);