CREATE TABLE accounts (
    id INTEGER primary key generated always as identity,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    unique (email)
);