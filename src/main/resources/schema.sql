drop table IF EXISTS items;
drop table IF EXISTS users;


CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT generated always as identity,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create table if not exists items
(
    id          BIGINT generated always as identity constraint item_pk primary key,
    name        VARCHAR(255) not null,
    description VARCHAR(255) not null,
    owner_id    BIGINT       not null constraint item_users_id_fk references users on update cascade on delete cascade,
    request_id  BIGINT,
    available   BOOLEAN
);


